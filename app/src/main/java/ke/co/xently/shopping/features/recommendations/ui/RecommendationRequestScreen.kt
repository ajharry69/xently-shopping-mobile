package ke.co.xently.shopping.features.recommendations.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.hasEmojis
import ke.co.xently.shopping.features.core.isRetryable
import ke.co.xently.shopping.features.core.loadingIndicatorLabel
import ke.co.xently.shopping.features.core.models.toLocation
import ke.co.xently.shopping.features.core.ui.LabeledCheckbox
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.locationtracker.ForegroundLocationTracker
import ke.co.xently.shopping.features.recommendations.models.Recommendation

@Composable
fun RecommendationRequestScreen(
    modifier: Modifier,
    viewModel: RecommendationViewModel,
    onSuccess: () -> Unit,
) {
    val draftShoppingListItemIndex: Int by viewModel.draftShoppingListItemIndex.collectAsState()
    val recommendationsState: State by viewModel.recommendationsStateFlow.collectAsState(State.Idle)
    val request: Recommendation.Request by viewModel.recommendationRequest.collectAsState()
    val draftShoppingListItem: Recommendation.Request.ShoppingListItem by viewModel.draftShoppingListItem.collectAsState()

    val snackbarHostState = LocalSnackbarHostState.current

    if (recommendationsState is State.GettingCurrentLocation) {
        ForegroundLocationTracker(
            snackbarHostState = snackbarHostState,
            onLocationUpdates = {
                viewModel.getRecommendations(it.toLocation())
            },
        )
    }

    RecommendationRequestScreen(
        draftShoppingListItem = draftShoppingListItem,
        request = request,
        recommendationsState = recommendationsState,
        modifier = modifier,
        draftShoppingListItemIndex = draftShoppingListItemIndex,
        onSuccess = onSuccess,
        saveDraftRecommendationRequest = viewModel::saveDraftRecommendationRequest,
        clearDraftShoppingListItem = viewModel::clearDraftShoppingListItem,
        getRecommendations = viewModel::flagGettingCurrentLocation,
        saveIndexedDraftShoppingListItem = viewModel::saveDraftShoppingListItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationRequestScreen(
    draftShoppingListItem: Recommendation.Request.ShoppingListItem,
    request: Recommendation.Request,
    recommendationsState: State,
    modifier: Modifier,
    draftShoppingListItemIndex: Int,
    onSuccess: () -> Unit,
    saveDraftRecommendationRequest: (request: Recommendation.Request) -> Unit,
    clearDraftShoppingListItem: () -> Unit,
    getRecommendations: () -> Unit,
    saveIndexedDraftShoppingListItem: (Recommendation.Request.ShoppingListItem, Int) -> Unit = { _, _ -> },
) {
    var shoppingListItemValue by remember(draftShoppingListItem.name) {
        mutableStateOf(TextFieldValue(draftShoppingListItem.name))
    }

    val shoppingList = remember(request.shoppingList) {
        mutableStateListOf(*request.shoppingList.toTypedArray())
    }

    val showEmptyShoppingListMessage by remember(request.shoppingList) {
        derivedStateOf {
            request.shoppingList.isEmpty()
        }
    }

    val uiState by produceState<RecommendationRequestUIState>(
        RecommendationRequestUIState.OK,
        shoppingListItemValue.text,
    ) {
        value = when {
            shoppingListItemValue.text.hasEmojis -> {
                RecommendationRequestUIState.NameError.ImojisNotAllowed
            }

            shoppingListItemValue.text.isBlank() -> {
                RecommendationRequestUIState.BlankNameNotAllowed
            }

            else -> {
                RecommendationRequestUIState.OK
            }
        }
    }

    val showAddButton by remember(shoppingListItemValue, uiState) {
        derivedStateOf {
            uiState is RecommendationRequestUIState.OK
        }
    }

    val recommendationsLoading by remember(recommendationsState) {
        derivedStateOf {
            recommendationsState is State.Loading
        }
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = LocalSnackbarHostState.current

    val currentOnSuccess by rememberUpdatedState(onSuccess)
    val currentGetRecommendations by rememberUpdatedState(getRecommendations)
    LaunchedEffect(recommendationsState) {
        if (recommendationsState is State.Success) {
            currentOnSuccess()
        } else if (recommendationsState is State.Failure) {
            val message = recommendationsState.error.localizedMessage
                ?: context.getString(R.string.xently_generic_error_message)
            val actionLabel = if (recommendationsState.error.isRetryable) {
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
                currentGetRecommendations()
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
                val onEnforceStrictMeasurementUnitChange: (Boolean) -> Unit = { enforce ->
                    saveIndexedDraftShoppingListItem(
                        draftShoppingListItem.copy(enforceStrictMeasurementUnit = enforce),
                        draftShoppingListItemIndex,
                    )
                }
                LabeledCheckbox(
                    modifier = Modifier.fillMaxWidth(),
                    checked = draftShoppingListItem.enforceStrictMeasurementUnit,
                    onCheckedChange = onEnforceStrictMeasurementUnitChange,
                ) {
                    Text(text = stringResource(R.string.xently_checkbox_label_enforce_strict_measurement_unit))
                }

                val addShoppingListItem: () -> Unit = {
                    draftShoppingListItem.copy(name = shoppingListItemValue.text).let {
                        if (draftShoppingListItemIndex == RecommendationViewModel.DEFAULT_SHOPPING_LIST_ITEM_INDEX) {
                            shoppingList.add(it)
                        } else {
                            shoppingList.set(draftShoppingListItemIndex, it)
                        }
                    }

                    request.copy(shoppingList = shoppingList.toList())
                        .let(saveDraftRecommendationRequest)
                    clearDraftShoppingListItem()
                    shoppingListItemValue = TextFieldValue("")
                }
                TextField(
                    maxLines = 1,
                    singleLine = true,
                    value = shoppingListItemValue,
                    onValueChange = {
                        shoppingListItemValue = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.xently_text_field_label_shopping_list_item_name))
                    },
                    isError = uiState is RecommendationRequestUIState.NameError,
                    supportingText = if (uiState is RecommendationRequestUIState.NameError) {
                        {
                            Text(text = uiState(context = LocalContext.current))
                        }
                    } else null,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (showAddButton) {
                                addShoppingListItem()
                            } else {
                                focusManager.clearFocus()
                            }
                        },
                    ),
                    trailingIcon = {
                        val contentDescription = stringResource(
                            R.string.xently_content_description_add_item,
                            draftShoppingListItem.name,
                        )
                        PlainTooltipBox(
                            tooltip = {
                                Text(text = contentDescription)
                            },
                        ) {
                            IconButton(
                                onClick = addShoppingListItem,
                                enabled = showAddButton,
                                modifier = Modifier.tooltipTrigger(),
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = contentDescription,
                                )
                            }
                        }
                    },
                )
                Divider()
            }
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = showEmptyShoppingListMessage,
                label = "RecommendationsAnimatedContent",
            ) { isShoppingListEmpty ->
                if (isShoppingListEmpty) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(R.string.xently_empty_shopping_list_message),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    val lazyListState = rememberLazyListState()
                    LaunchedEffect(request.shoppingList) {
                        val index = maxOf(0, request.shoppingList.lastIndex)
                        lazyListState.animateScrollToItem(index)
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(request.shoppingList) { index, item: Recommendation.Request.ShoppingListItem ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = if (item.enforceStrictMeasurementUnit) {
                                            stringResource(R.string.xently_strict_measurement_unit)
                                        } else {
                                            stringResource(R.string.xently_optional_measurement_unit)
                                        },
                                    )
                                },
                                trailingContent = {
                                    var showShoppingListItemMenu by remember {
                                        mutableStateOf(false)
                                    }
                                    Box {
                                        IconButton(
                                            onClick = {
                                                showShoppingListItemMenu = true
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
                                            expanded = showShoppingListItemMenu,
                                            onDismissRequest = {
                                                showShoppingListItemMenu = false
                                            },
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(text = stringResource(R.string.xently_edit))
                                                },
                                                onClick = {
                                                    saveIndexedDraftShoppingListItem(item, index)
                                                    showShoppingListItemMenu = false
                                                },
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(text = stringResource(R.string.xently_remove))
                                                },
                                                onClick = {
                                                    shoppingList.removeAt(index)
                                                    request.copy(shoppingList = shoppingList.toList())
                                                        .let(saveDraftRecommendationRequest)
                                                    showShoppingListItemMenu = false
                                                },
                                            )
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Divider()
            val isGettingCurrentLocation by remember(recommendationsState) {
                derivedStateOf {
                    recommendationsState is State.GettingCurrentLocation
                }
            }
            val enableGetRecommendationsButton by remember(
                recommendationsLoading,
                isGettingCurrentLocation,
                request,
            ) {
                derivedStateOf {
                    !recommendationsLoading
                            && !isGettingCurrentLocation
                            && request.shoppingList.isNotEmpty()
                }
            }
            Button(
                enabled = enableGetRecommendationsButton,
                modifier = Modifier.fillMaxWidth(),
                onClick = getRecommendations,
            ) {
                val (label, loadingLabelPrefix) = if (isGettingCurrentLocation) {
                    R.string.xently_button_label_get_your_current_location to R.string.xently_button_label_getting_current_location
                } else {
                    R.string.xently_button_label_get_recommendations to R.string.xently_button_label_getting_recommendations
                }
                Text(
                    text = loadingIndicatorLabel(
                        loading = recommendationsLoading || isGettingCurrentLocation,
                        label = stringResource(label).toUpperCase(Locale.current),
                        loadingLabelPrefix = stringResource(loadingLabelPrefix),
                        keys = arrayOf(recommendationsState),
                    ),
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationRequestScreenPreview() {
    XentlyTheme {
        RecommendationRequestScreen(
            draftShoppingListItem = Recommendation.Request.ShoppingListItem.default,
            request = Recommendation.Request.default,
            recommendationsState = State.Idle,
            modifier = Modifier.fillMaxSize(),
            draftShoppingListItemIndex = RecommendationViewModel.DEFAULT_SHOPPING_LIST_ITEM_INDEX,
            onSuccess = {},
            saveDraftRecommendationRequest = {},
            clearDraftShoppingListItem = {},
            getRecommendations = {},
        )
    }
}