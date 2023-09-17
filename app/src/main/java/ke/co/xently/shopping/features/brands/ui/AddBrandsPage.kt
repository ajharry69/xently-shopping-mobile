package ke.co.xently.shopping.features.brands.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.brands.models.Brand
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextField
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.products.models.Product

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddBrandsPage(
    modifier: Modifier,
    brands: List<Brand>,
    saveDraft: (List<Brand>) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<Brand>) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState()
    val manufacturers = remember {
        mutableStateListOf(*brands.toTypedArray())
    }

    val currentSaveDraft by rememberUpdatedState(saveDraft)
    LaunchedEffect(manufacturers) {
        currentSaveDraft(manufacturers)
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_add_brands_page_title,
        subheading = R.string.xently_add_brands_page_sub_heading,
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
        val doSearch: () -> Brand = {
            Brand.LocalViewModel.default.copy(name = nameAutoCompleteState.query)
        }
        AutoCompleteTextField(
            state = nameAutoCompleteState,
            service = LocalBrandAutoCompleteService.current,
            onSuggestionSelected = {
                manufacturers.add(it)
                nameAutoCompleteState.resetQuery()
            },
            onSearch = {
                doSearch()
            },
            modifier = Modifier.fillMaxWidth(),
            queryToResponse = { it },
            label = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
            trailingIcon = if (nameAutoCompleteState.query.isNotBlank()) {
                {
                    IconButton(
                        onClick = {
                            Brand.LocalViewModel.default
                                .copy(name = nameAutoCompleteState.query)
                                .let(manufacturers::add)
                            nameAutoCompleteState.resetQuery()
                        },
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.xently_content_description_add_brand),
                        )
                    }
                }
            } else null,
            suggestionContent = {
                Text(text = it.name)
            },
        )

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
                        onClick = {},
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
private fun AddBrandsPagePreview() {
    XentlyTheme {
        AddBrandsPage(
            modifier = Modifier.fillMaxSize(),
            brands = Product.LocalViewModel.default.brands,
            saveDraft = {},
            onPreviousClick = {},
        ) {}
    }
}