package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddBrandsPage(
    modifier: Modifier,
    brands: List<Brand>,
    suggestionsState: StateFlow<List<Brand>>,
    search: (Brand) -> Unit,
    saveDraft: (List<Brand>) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<Brand>) -> Unit,
) {
    val suggestions by suggestionsState.collectAsState()
    var brandNameQuery by remember {
        mutableStateOf("")
    }
    val brandNameQueryNotBlank by remember(brandNameQuery) {
        derivedStateOf {
            brandNameQuery.isNotBlank()
        }
    }
    val brandNameSearchActive by remember(brandNameQuery, suggestions) {
        derivedStateOf {
            brandNameQuery.isNotBlank() && suggestions.isNotEmpty()
        }
    }
    val manufacturers = remember {
        mutableStateListOf(*brands.toTypedArray())
    }

    LaunchedEffect(manufacturers) {
        saveDraft(manufacturers)
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_brands_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onContinueClick(manufacturers)
                },
            ) {
                Text(text = stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        val doSearch: () -> Unit by rememberUpdatedState {
            Brand.LocalViewModel.default.copy(name = brandNameQuery)
                .let(search)
        }
        DockedSearchBar(
            query = brandNameQuery,
            onQueryChange = {
                brandNameQuery = it
                doSearch()
            },
            onSearch = {
                doSearch()
            },
            active = brandNameSearchActive,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
            placeholder = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
            trailingIcon = if (brandNameQueryNotBlank) {
                {
                    IconButton(
                        onClick = {
                            Brand.LocalViewModel.default
                                .copy(name = brandNameQuery)
                                .let(manufacturers::add)
                            brandNameQuery = ""
                        },
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.xently_content_description_add_brand),
                        )
                    }
                }
            } else null
        ) {
            for (suggestion in suggestions) {
                ListItem(
                    headlineContent = {
                        Text(text = suggestion.name)
                    },
                    modifier = Modifier.clickable {
                        manufacturers.add(suggestion)
                    },
                )
            }
        }

        val visible by remember {
            derivedStateOf {
                manufacturers.isNotEmpty()
            }
        }

        AnimatedVisibility(visible = visible) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for ((i, brand) in manufacturers.withIndex()) {
                    AssistChip(
                        onClick = {
                            brandNameQuery = brand.name
                        },
                        label = {
                            Text(text = brand.toString())
                        },
                        trailingIcon = {
                            IconButton(onClick = { manufacturers.removeAt(i) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.xently_content_description_remove_item,
                                        brand,
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
fun AddBrandsPagePreview() {
    XentlyTheme {
        AddBrandsPage(
            modifier = Modifier.fillMaxSize(),
            brands = Product.LocalViewModel.default.brands,
            suggestionsState = MutableStateFlow(emptyList()),
            search = {},
            saveDraft = {},
            onPreviousClick = {},
            onContinueClick = {},
        )
    }
}