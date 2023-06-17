package ke.co.xently.products.ui.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.ui.components.AddProductPage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAttributesPage(
    modifier: Modifier = Modifier,
    attributes: List<AttributeValue>,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<AttributeValue>) -> Unit,
) {
    val attrs = remember {
        mutableStateListOf(*attributes.toTypedArray())
    }
    var attributeNameQuery by remember {
        mutableStateOf("")
    }
    var attributeValueQuery by remember {
        mutableStateOf("")
    }
    val attributeNameSearchActive by remember(attributeNameQuery) {
        derivedStateOf {
            attributeNameQuery.isNotBlank()
        }
    }
    val attributeValueSearchActive by remember(attributeValueQuery) {
        derivedStateOf {
            attributeValueQuery.isNotBlank()
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_attributes_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onContinueClick(attrs)
                },
            ) {
                Text(text = stringResource(R.string.xently_button_label_submit))
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DockedSearchBar(
                query = attributeNameQuery,
                onQueryChange = {
                    attributeNameQuery = it
                },
                onSearch = {
                    // TODO: Trigger search
                },
                active = attributeNameSearchActive,
                onActiveChange = {},
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                placeholder = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
                },
            ) {

            }
            DockedSearchBar(
                query = attributeValueQuery,
                onQueryChange = {
                    attributeValueQuery = it
                },
                onSearch = {
                    // TODO: Trigger search
                },
                active = attributeValueSearchActive,
                onActiveChange = {},
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                placeholder = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_value))
                },
            ) {

            }

            val visible by remember {
                derivedStateOf {
                    attrs.isNotEmpty()
                }
            }

            AnimatedVisibility(visible = visible) {
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    for (attribute in attrs) {
                        SuggestionChip(
                            onClick = {
                                attributeValueQuery = attribute.value
                                attributeNameQuery = attribute.attribute.name
                            },
                            label = {
                                Text(text = attribute.toString())
                            },
                        )
                    }
                }
            }
        }
    }
}