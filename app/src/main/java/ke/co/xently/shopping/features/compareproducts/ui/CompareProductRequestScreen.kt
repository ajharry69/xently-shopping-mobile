package ke.co.xently.shopping.features.compareproducts.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import ke.co.xently.shopping.BottomSheet
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.compareproducts.models.CompareProduct
import ke.co.xently.shopping.features.compareproducts.models.ComparisonListItem
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.core.cleansedForNumberParsing
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.isRetryable
import ke.co.xently.shopping.features.core.loadingIndicatorLabel
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme

@Composable
fun CompareProductsRequestScreen(
    modifier: Modifier,
    viewModel: CompareProductViewModel,
    bottomSheetPeek: (BottomSheet) -> Unit,
) {
    val draftComparisonListItemIndex: Int by viewModel.draftComparisonListItemIndex.collectAsState()
    val comparisonsState: State by viewModel.comparisonsStateFlow.collectAsState(State.Idle)
    val request: CompareProduct.Request by viewModel.comparisonRequest.collectAsState()
    val draftComparisonListItem: ComparisonListItem by viewModel.draftComparisonListItem.collectAsState()

    CompareProductsRequestScreen(
        draftComparisonListItem = draftComparisonListItem,
        request = request,
        comparisonsState = comparisonsState,
        modifier = modifier,
        draftComparisonListItemIndex = draftComparisonListItemIndex,
        bottomSheetPeek = bottomSheetPeek,
        saveDraftCompareProductsRequest = viewModel::saveDraftCompareProductsRequest,
        clearDraftComparisonListItem = viewModel::clearDraftComparisonListItem,
        compareProducts = viewModel::compareProducts,
        saveIndexedDraftComparisonListItem = viewModel::saveDraftComparisonListItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompareProductsRequestScreen(
    draftComparisonListItem: ComparisonListItem,
    request: CompareProduct.Request,
    comparisonsState: State,
    modifier: Modifier,
    draftComparisonListItemIndex: Int,
    bottomSheetPeek: (BottomSheet) -> Unit,
    saveDraftCompareProductsRequest: (request: CompareProduct.Request) -> Unit,
    clearDraftComparisonListItem: () -> Unit,
    compareProducts: () -> Unit,
    saveIndexedDraftComparisonListItem: (ComparisonListItem, Int) -> Unit = { _, _ -> },
) {
    var comparisonListItemNameValue by remember(draftComparisonListItem.name) {
        mutableStateOf(TextFieldValue(draftComparisonListItem.name))
    }
    var comparisonListItemUnitPriceValue by remember(draftComparisonListItem.unitPrice) {
        val text = draftComparisonListItem.unitPrice.takeIf { it.toFloat() > 0 }?.toString() ?: ""
        mutableStateOf(TextFieldValue(text))
    }
    val comparisonList = remember(request.comparisonList) {
        mutableStateListOf(*request.comparisonList.toTypedArray())
    }

    val showEmptyComparisonListMessage by remember(request.comparisonList) {
        derivedStateOf {
            request.comparisonList.isEmpty()
        }
    }

    val comparisonsLoading by remember(comparisonsState) {
        derivedStateOf {
            comparisonsState is State.Loading
        }
    }

    val uiState by produceState<CompareProductRequestUIState>(
        CompareProductRequestUIState.OK,
        comparisonListItemUnitPriceValue.text,
        comparisonListItemNameValue.text,
    ) {
        value = when {
            comparisonListItemUnitPriceValue.text.isBlank() && comparisonListItemNameValue.text.isBlank() -> {
                CompareProductRequestUIState.NameAndUnitPriceBlank
            }

            comparisonListItemUnitPriceValue.text.isBlank() -> {
                CompareProductRequestUIState.MissingUnitPrice
            }

            comparisonListItemUnitPriceValue.text.cleansedForNumberParsing()
                .toBigDecimalOrNull() == null -> {
                CompareProductRequestUIState.InvalidUnitPrice
            }

            comparisonListItemNameValue.text.isBlank() -> {
                CompareProductRequestUIState.MissingName
            }

            else -> {
                CompareProductRequestUIState.OK
            }
        }
    }

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current
    val currentCompareProducts by rememberUpdatedState(compareProducts)
    val currentBottomSheetPeek by rememberUpdatedState(bottomSheetPeek)

    LaunchedEffect(comparisonsState) {
        if (comparisonsState is State.Success) {
            currentBottomSheetPeek(BottomSheet.CompareProductResponse(comparisonsState.data))
        } else if (comparisonsState is State.Failure) {
            val message = comparisonsState.error.localizedMessage
                ?: context.getString(R.string.xently_generic_error_message)
            val actionLabel = if (comparisonsState.error.isRetryable) {
                context.getString(R.string.xently_retry).toUpperCase(Locale.current)
            } else {
                null
            }
            val result: SnackbarResult = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long,
            )

            if (result == SnackbarResult.ActionPerformed) {
                currentCompareProducts()
            }
        }
    }

    Column(modifier = Modifier.then(modifier)) {
        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextField(
                        maxLines = 1,
                        singleLine = true,
                        value = comparisonListItemNameValue,
                        onValueChange = {
                            comparisonListItemNameValue = it
                        },
                        modifier = Modifier.weight(1f),
                        isError = uiState is CompareProductRequestUIState.NameError,
                        label = {
                            Text(text = stringResource(R.string.xently_text_field_label_name_required))
                        },
                        supportingText = {
                            val message = if (uiState is CompareProductRequestUIState.NameError) {
                                uiState(context = LocalContext.current)
                            } else {
                                stringResource(R.string.xently_text_field_help_text_comparison_list_item_name)
                            }
                            Text(text = message)
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    )
                    TextField(
                        maxLines = 1,
                        singleLine = true,
                        value = comparisonListItemUnitPriceValue,
                        onValueChange = {
                            comparisonListItemUnitPriceValue = it
                        },
                        modifier = Modifier.weight(1f),
                        isError = uiState is CompareProductRequestUIState.UnitPriceError,
                        label = {
                            Text(text = stringResource(R.string.xently_text_field_label_unit_price_required))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                        supportingText = if (uiState is CompareProductRequestUIState.UnitPriceError) {
                            {
                                Text(text = uiState(context = LocalContext.current))
                            }
                        } else {
                            null
                        },
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState is CompareProductRequestUIState.OK,
                    onClick = {
                        draftComparisonListItem.copy(
                            name = comparisonListItemNameValue.text.trim(),
                            unitPrice = comparisonListItemUnitPriceValue.text
                                .cleansedForNumberParsing()
                                .toBigDecimal(),
                        ).let {
                            if (draftComparisonListItemIndex == CompareProductViewModel.DEFAULT_COMPARISON_LIST_ITEM_INDEX) {
                                comparisonList.add(it)
                            } else {
                                comparisonList.set(draftComparisonListItemIndex, it)
                            }
                        }

                        request.copy(comparisonList = comparisonList.toList())
                            .let(saveDraftCompareProductsRequest)
                        clearDraftComparisonListItem()
                        comparisonListItemNameValue = TextFieldValue("")
                        comparisonListItemUnitPriceValue = TextFieldValue("")
                    },
                ) {
                    Text(text = stringResource(R.string.xently_button_label_add_to_comparison_list))
                }
                Divider()
            }
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = showEmptyComparisonListMessage,
                label = "ComparisonRequestListAnimatedContent",
            ) { isComparisonListEmpty ->
                if (isComparisonListEmpty) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(R.string.xently_empty_comparison_list_message),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    val lazyListState = rememberLazyListState()
                    LaunchedEffect(request.comparisonList) {
                        val index = maxOf(0, request.comparisonList.lastIndex)
                        lazyListState.animateScrollToItem(index)
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(request.comparisonList) { index, item: ComparisonListItem ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                },
                                trailingContent = {
                                    var showComparisonListItemMenu by remember {
                                        mutableStateOf(false)
                                    }
                                    Box {
                                        IconButton(
                                            onClick = {
                                                showComparisonListItemMenu = true
                                            },
                                        ) {
                                            Icon(
                                                Icons.Default.MoreVert,
                                                contentDescription = stringResource(
                                                    R.string.xently_content_description_options_for_item,
                                                    item,
                                                ),
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = showComparisonListItemMenu,
                                            onDismissRequest = {
                                                showComparisonListItemMenu = false
                                            },
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(text = stringResource(R.string.xently_edit))
                                                },
                                                onClick = {
                                                    saveIndexedDraftComparisonListItem(item, index)
                                                    showComparisonListItemMenu = false
                                                },
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(text = stringResource(R.string.xently_remove))
                                                },
                                                onClick = {
                                                    comparisonList.removeAt(index)
                                                    request.copy(comparisonList = comparisonList.toList())
                                                        .let(saveDraftCompareProductsRequest)
                                                    showComparisonListItemMenu = false
                                                },
                                            )
                                        }
                                    }
                                },
                                supportingContent = {
                                    Text(
                                        text = LocalContext.current
                                            .currencyNumberFormat.format(item.unitPrice),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Divider()
            Surface(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = stringResource(R.string.xently_content_description_sort_compare_product_responses),
                    )
                    Column {
                        val label by remember(request.orderBy) {
                            derivedStateOf {
                                when (request.orderBy) {
                                    OrderBy.Ascending -> R.string.xently_compare_product_response_sort_by_cheapest_first
                                    OrderBy.Descending -> R.string.xently_compare_product_response_sort_by_expensive_first
                                }
                            }
                        }
                        Text(text = stringResource(label))
                        PlainTooltipBox(
                            tooltip = {
                                Text(
                                    text = stringResource(
                                        R.string.xently_switch_order,
                                        stringResource(request.orderBy.opposite.label)
                                            .toLowerCase(Locale.current),
                                    ),
                                )
                            },
                        ) {
                            Text(
                                text = stringResource(request.orderBy.label),
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .tooltipTrigger()
                                    .toggleable(
                                        value = true,
                                        role = Role.Switch,
                                        onValueChange = {
                                            request
                                                .copy(orderBy = request.orderBy.opposite)
                                                .let(saveDraftCompareProductsRequest)
                                        },
                                    ),
                            )
                        }
                    }
                }
            }
            val enableGetCompareProductsButton by remember(comparisonsLoading, request) {
                derivedStateOf {
                    !comparisonsLoading && request.comparisonList.size > 1
                }
            }
            Button(
                enabled = enableGetCompareProductsButton,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    compareProducts()
                    Firebase.performance.newTrace("compare_products_request")
                },
            ) {
                Text(
                    text = loadingIndicatorLabel(
                        loading = comparisonsLoading,
                        label = if (request.comparisonList.size == 1) {
                            stringResource(R.string.xently_button_label_add_another_item)
                        } else {
                            stringResource(R.string.xently_button_label_compare_products)
                                .toUpperCase(Locale.current)
                        },
                        loadingLabelPrefix = stringResource(R.string.xently_button_label_comparing_products),
                        keys = arrayOf(comparisonsState),
                    ),
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun CompareProductsRequestScreenPreview() {
    XentlyTheme {
        CompareProductsRequestScreen(
            draftComparisonListItem = ComparisonListItem.default,
            request = CompareProduct.Request.default,
            comparisonsState = State.Idle,
            modifier = Modifier.fillMaxSize(),
            draftComparisonListItemIndex = CompareProductViewModel.DEFAULT_COMPARISON_LIST_ITEM_INDEX,
            bottomSheetPeek = {},
            saveDraftCompareProductsRequest = {},
            clearDraftComparisonListItem = {},
            compareProducts = {},
        )
    }
}
