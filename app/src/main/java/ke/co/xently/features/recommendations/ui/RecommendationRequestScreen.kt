package ke.co.xently.features.recommendations.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.R
import ke.co.xently.features.core.ui.LabeledCheckbox
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.ui.loadingIndicatorLabel
import ke.co.xently.ui.theme.XentlyTheme

@Composable
fun RecommendationRequestScreen(
    modifier: Modifier,
    viewModel: RecommendationViewModel = hiltViewModel(),
) {
    val draftShoppingListItemIndex: Int by viewModel.draftShoppingListItemIndex.collectAsState()
    val recommendationsState: State by viewModel.recommendationsState.collectAsState()
    val request: Recommendation.Request by viewModel.recommendationRequest.collectAsState()
    val draftShoppingListItem: Recommendation.Request.ShoppingListItem by viewModel.draftShoppingListItem.collectAsState()

    RecommendationRequestScreen(
        draftShoppingListItem = draftShoppingListItem,
        request = request,
        recommendationsState = recommendationsState,
        modifier = modifier,
        saveIndexedDraftShoppingListItem = viewModel::saveDraftShoppingListItem,
        draftShoppingListItemIndex = draftShoppingListItemIndex,
        saveDraftRecommendationRequest = viewModel::saveDraftRecommendationRequest,
        clearDraftShoppingListItem = viewModel::clearDraftShoppingListItem,
        getRecommendations = viewModel::getRecommendations,
    )
}

@Composable
fun RecommendationRequestScreen(
    draftShoppingListItem: Recommendation.Request.ShoppingListItem,
    request: Recommendation.Request,
    recommendationsState: State,
    modifier: Modifier,
    draftShoppingListItemIndex: Int,
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

    val showAddButton by remember(shoppingListItemValue) {
        derivedStateOf {
            shoppingListItemValue.text.isNotBlank()
        }
    }

    val recommendationsLoading by remember(recommendationsState) {
        derivedStateOf {
            recommendationsState is State.Loading
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
                val onEnforceStrictMeasurementUnitChange: (Boolean) -> Unit by rememberUpdatedState { enforce ->
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

                val addShoppingListItem: () -> Unit by rememberUpdatedState {
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
                var imeActionClickedOnce by remember {
                    mutableStateOf(false)
                }
                val focusManager = LocalFocusManager.current
                TextField(
                    value = shoppingListItemValue,
                    onValueChange = {
                        shoppingListItemValue = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.xently_text_field_label_shopping_list_item_name))
                    },
                    supportingText = {
                        Text(text = stringResource(R.string.xently_text_field_help_text_shopping_list_item_name))
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (imeActionClickedOnce) {
                                // Apparently, we need to click the IME action once before the
                                // field can be cleared
                                imeActionClickedOnce = false
                                if (showAddButton) {
                                    addShoppingListItem()
                                } else {
                                    focusManager.clearFocus()
                                }
                            } else {
                                imeActionClickedOnce = true
                            }
                        },
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    trailingIcon = if (showAddButton) {
                        {
                            IconButton(onClick = addShoppingListItem) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(
                                        R.string.xently_content_description_add_item,
                                        draftShoppingListItem.name,
                                    ),
                                )
                            }
                        }
                    } else null,
                )
                Divider()
            }
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = showEmptyShoppingListMessage,
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
                        lazyListState.animateScrollToItem(request.shoppingList.lastIndex)
                    }

                    LazyColumn(
                        reverseLayout = true,
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
            val enableGetRecommendationsButton by remember(recommendationsLoading, request) {
                derivedStateOf {
                    !recommendationsLoading && request.shoppingList.isNotEmpty()
                }
            }
            Button(
                enabled = enableGetRecommendationsButton,
                modifier = Modifier.fillMaxWidth(),
                onClick = getRecommendations,
            ) {
                Text(
                    text = loadingIndicatorLabel(
                        loading = recommendationsLoading,
                        label = stringResource(R.string.xently_button_label_get_recommendations)
                            .toUpperCase(Locale.current),
                        loadingLabelPrefix = stringResource(R.string.xently_button_label_getting_recommendations),
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
fun RecommendationRequestScreenPreview() {
    XentlyTheme {
        RecommendationRequestScreen(
            modifier = Modifier.fillMaxSize(),
            draftShoppingListItem = Recommendation.Request.ShoppingListItem.default,
            request = Recommendation.Request.default,
            recommendationsState = State.Idle,
            draftShoppingListItemIndex = RecommendationViewModel.DEFAULT_SHOPPING_LIST_ITEM_INDEX,
            saveDraftRecommendationRequest = {},
            clearDraftShoppingListItem = {},
            getRecommendations = {},
        )
    }
}