package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.State
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAttributesPage(
    modifier: Modifier = Modifier,
    stateState: StateFlow<State>,
    attributes: List<AttributeValue>,
    suggestionsState: StateFlow<List<AttributeValue>>,
    search: (AttributeValue) -> Unit,
    saveDraft: (List<AttributeValue>) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<AttributeValue>) -> Unit,
) {
    val state by stateState.collectAsState()
    val suggestions by suggestionsState.collectAsState()
    val attrs = remember {
        mutableStateListOf(*attributes.toTypedArray())
    }
    var attributeNameQuery by remember {
        mutableStateOf("")
    }
    var attributeValueQuery by remember {
        mutableStateOf("")
    }
    val attributeNameSearchActive by remember(attributeNameQuery, suggestions) {
        derivedStateOf {
            attributeNameQuery.isNotBlank() && suggestions.isNotEmpty()
        }
    }
    val attributeValueSearchActive by remember(attributeValueQuery, suggestions) {
        derivedStateOf {
            attributeValueQuery.isNotBlank() && suggestions.isNotEmpty()
        }
    }
    val attributeNameQueryNotBlank by remember(attributeNameQuery) {
        derivedStateOf {
            attributeNameQuery.isNotBlank()
        }
    }
    val attributeValueQueryNotBlank by remember(attributeValueQuery) {
        derivedStateOf {
            attributeValueQuery.isNotBlank()
        }
    }

    LaunchedEffect(attrs) {
        saveDraft(attrs)
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_attributes_page_title,
        onBackClick = onPreviousClick,
        enableBackButton = state !is State.Loading,
        continueButton = {
            if (state is State.Loading) {
                Button(
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                ) {
                    Text(text = stringResource(R.string.xently_button_label_loading))
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onContinueClick(attrs)
                    },
                ) {
                    Text(text = stringResource(R.string.xently_button_label_submit))
                }
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val doSearch: () -> Unit by rememberUpdatedState {
                val attribute = AttributeValue.LocalViewModel.default.run {
                    copy(
                        value = attributeValueQuery,
                        attribute = attribute.copy(name = attributeNameQuery),
                    )
                }
                search(attribute)
            }
            val content: @Composable (ColumnScope.() -> Unit) = {
                for (suggestion in suggestions) {
                    ListItem(
                        headlineContent = {
                            Text(text = suggestion.attribute.name)
                        },
                        supportingContent = {
                            Text(text = suggestion.value)
                        },
                        modifier = Modifier.clickable {
                            attrs.add(suggestion)
                        },
                    )
                }
            }
            val trailingIcon: (@Composable () -> Unit)? =
                if (attributeNameQueryNotBlank && attributeValueQueryNotBlank) {
                    {
                        IconButton(
                            onClick = {
                                AttributeValue.LocalViewModel.default.run {
                                    copy(
                                        value = attributeValueQuery,
                                        attribute = attribute.copy(name = attributeNameQuery),
                                    )
                                }.let(attrs::add)
                                attributeNameQuery = ""
                                attributeValueQuery = ""
                            },
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.xently_content_description_add_attribute),
                            )
                        }
                    }
                } else null
            DockedSearchBar(
                query = attributeNameQuery,
                onQueryChange = {
                    attributeNameQuery = it
                    doSearch()
                },
                onSearch = {
                    doSearch()
                },
                active = attributeNameSearchActive,
                onActiveChange = {},
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                placeholder = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
                },
                trailingIcon = trailingIcon,
                content = content,
            )

            DockedSearchBar(
                query = attributeValueQuery,
                onQueryChange = {
                    attributeValueQuery = it
                    doSearch()
                },
                onSearch = {
                    doSearch()
                },
                active = attributeValueSearchActive,
                onActiveChange = {},
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                placeholder = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_value))
                },
                trailingIcon = trailingIcon,
                content = content,
            )
        }

        val visible by remember {
            derivedStateOf {
                attrs.isNotEmpty()
            }
        }

        AnimatedVisibility(visible = visible, modifier = Modifier.fillMaxWidth()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for ((i, attribute) in attrs.withIndex()) {
                    AssistChip(
                        onClick = {
                            attributeValueQuery = attribute.value
                            attributeNameQuery = attribute.attribute.name
                        },
                        label = {
                            Text(text = attribute.toString())
                        },
                        trailingIcon = {
                            IconButton(onClick = { attrs.removeAt(i) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.xently_content_description_remove_item,
                                        attribute,
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AddAttributesPagePreview() {
    XentlyTheme {
        AddAttributesPage(
            modifier = Modifier.fillMaxSize(),
            attributes = Product.LocalViewModel.default.attributes,
            stateState = MutableStateFlow(State.Idle),
            suggestionsState = MutableStateFlow(emptyList()),
            search = {},
            saveDraft = {},
            onPreviousClick = {},
            onContinueClick = {},
        )
    }
}