package ke.co.xently.recommendations.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.R
import ke.co.xently.recommendations.models.Recommendation

@Composable
fun RecommendationRequestScreen(
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = hiltViewModel(),
) {
    val request by viewModel.recommendationRequest.collectAsState()
    val draftShoppingListItem by viewModel.draftShoppingListItem.collectAsState()
    var shoppingListItemValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    val shoppingList = remember(request.shoppingList) {
        mutableStateListOf(*request.shoppingList.toTypedArray())
    }

    val showEmptyShoppingListMessage by remember(request.shoppingList) {
        derivedStateOf {
            request.shoppingList.isEmpty()
        }
    }

    val showAddButton by remember {
        derivedStateOf {
            shoppingListItemValue.text.isNotBlank()
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
                Surface(
                    checked = draftShoppingListItem.enforceStrictMeasurementUnit,
                    onCheckedChange = {
                        draftShoppingListItem.copy(enforceStrictMeasurementUnit = it)
                            .let(viewModel::saveDraftShoppingListItem)
                    },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(
                            checked = draftShoppingListItem.enforceStrictMeasurementUnit,
                            onCheckedChange = {
                                draftShoppingListItem.copy(enforceStrictMeasurementUnit = it)
                                    .let(viewModel::saveDraftShoppingListItem)
                            },
                        )
                        Text(text = stringResource(R.string.xently_checkbox_label_enforce_strict_measurement_unit))
                    }
                }
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
                    trailingIcon = if (showAddButton) {
                        {
                            IconButton(
                                onClick = {
                                    draftShoppingListItem.copy(name = shoppingListItemValue.text)
                                        .let(shoppingList::add)

                                    request.copy(shoppingList = shoppingList.toList())
                                        .let(viewModel::saveDraftRecommendationRequest)
                                    viewModel.clearDraftShoppingListItem()
                                    shoppingListItemValue = TextFieldValue("")
                                },
                            ) {
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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(request.shoppingList) { i, item: Recommendation.Request.ShoppingListItem ->
                            ListItem(
                                headlineContent = {
                                    Text(text = item.name)
                                },
                                overlineContent = {
                                    Text(
                                        text = if (item.enforceStrictMeasurementUnit) {
                                            stringResource(R.string.xently_strict_measurement_unit)
                                        } else {
                                            stringResource(R.string.xently_optional_measurement_unit)
                                        },
                                    )
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            shoppingList.removeAt(i)
                                            request.copy(shoppingList = shoppingList.toList())
                                                .let(viewModel::saveDraftRecommendationRequest)
                                        },
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(
                                                R.string.xently_content_description_remove_item,
                                                item,
                                            ),
                                        )
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::getRecommendations,
            ) {
                Text(
                    text = stringResource(R.string.xently_button_label_get_recommendations)
                        .toUpperCase(Locale.current),
                )
            }
        }
    }
}