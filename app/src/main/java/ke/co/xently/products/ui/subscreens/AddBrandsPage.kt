package ke.co.xently.products.ui.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import ke.co.xently.products.models.Brand
import ke.co.xently.products.ui.components.AddProductPage

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddBrandsPage(
    modifier: Modifier = Modifier,
    brands: List<Brand>,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<Brand>) -> Unit,
) {
    val manufacturers = remember {
        mutableStateListOf(*brands.toTypedArray())
    }
    var brandNameQuery by remember {
        mutableStateOf("")
    }
    val brandNameSearchActive by remember(brandNameQuery) {
        derivedStateOf {
            brandNameQuery.isNotBlank()
        }
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
        DockedSearchBar(
            query = brandNameQuery,
            onQueryChange = {
                brandNameQuery = it
            },
            onSearch = {
                // TODO: Trigger search
            },
            active = brandNameSearchActive,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
            placeholder = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
        ) {

        }

        val visible by remember {
            derivedStateOf {
                manufacturers.isNotEmpty()
            }
        }

        AnimatedVisibility(visible = visible) {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                for (brand in manufacturers) {
                    SuggestionChip(
                        onClick = {
                            brandNameQuery = brand.name
                        },
                        label = {
                            Text(text = brand.toString())
                        },
                    )
                }
            }
        }
    }
}