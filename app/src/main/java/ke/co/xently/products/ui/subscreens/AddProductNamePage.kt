package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.products.models.Product
import ke.co.xently.products.models.ProductName
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.products.ui.components.AutoCompleteTextField
import ke.co.xently.products.ui.components.LabeledCheckbox
import ke.co.xently.products.ui.components.rememberAutoCompleteTextFieldState
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private sealed interface ProductNameContinueButtonLabel {
    @get:StringRes
    val label: Int

    object Continue : ProductNameContinueButtonLabel {
        override val label: Int = R.string.xently_button_label_continue
    }

    object MissingProductName : ProductNameContinueButtonLabel {
        override val label: Int = R.string.xently_button_label_missing_product_name
    }
}

@Composable
fun AddProductNamePage(
    modifier: Modifier = Modifier,
    product: Product,
    suggestionsState: StateFlow<List<Product>>,
    search: (ProductName) -> Unit,
    saveDraft: (Product) -> Unit,
    onSearchSuggestionSelected: () -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = product.name.name,
        suggestionsState = suggestionsState
    )

    var namePlural by remember(product.name.namePlural) {
        mutableStateOf(TextFieldValue(product.name.namePlural ?: ""))
    }

    var autoFillPlural by rememberSaveable(product.toLocalViewModel().autoFillNamePlural) {
        mutableStateOf(product.toLocalViewModel().autoFillNamePlural)
    }

    var buttonLabel by remember {
        mutableStateOf<ProductNameContinueButtonLabel>(ProductNameContinueButtonLabel.Continue)
    }

    LaunchedEffect(nameAutoCompleteState.query) {
        buttonLabel = if (nameAutoCompleteState.query.isBlank()) {
            ProductNameContinueButtonLabel.MissingProductName
        } else {
            ProductNameContinueButtonLabel.Continue
        }
    }

    LaunchedEffect(nameAutoCompleteState.query) {
        if (autoFillPlural) {
            namePlural = if (nameAutoCompleteState.query.isBlank()) {
                TextFieldValue()
            } else {
                val plural = buildString {
                    append(nameAutoCompleteState.query.trim())
                    append('s')
                }
                namePlural.copy(plural, selection = TextRange(plural.lastIndex))
            }
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_product_name_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            val enabled by remember(buttonLabel) {
                derivedStateOf {
                    buttonLabel is ProductNameContinueButtonLabel.Continue
                }
            }
            Button(
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    product.toLocalViewModel().run {
                        copy(
                            autoFillNamePlural = autoFillPlural,
                            name = name.copy(
                                name = nameAutoCompleteState.query,
                                namePlural = namePlural.text.takeIf { it.isNotBlank() },
                            ),
                        )
                    }.let(onContinueClick)
                },
            ) {
                Text(stringResource(buttonLabel.label))
            }
        },
    ) {
        AutoCompleteTextField(
            modifier = Modifier.fillMaxWidth(),
            state = nameAutoCompleteState,
            onSearch = { query ->
                ProductName.LocalViewModel.default.copy(name = query)
                    .let(search)
            },
            saveDraft = saveDraft,
            onSearchSuggestionSelected = onSearchSuggestionSelected,
            suggestionContent = {
                Text(text = it.descriptiveName.ifBlank { it.name.name })
            },
            placeholderContent = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name_required))
            },
        )

        LabeledCheckbox(
            checked = autoFillPlural,
            onCheckedChange = { autoFillPlural = it },
        ) {
            Text(text = stringResource(R.string.xently_checkbox_label_autofill_plural))
        }

        TextField(
            value = namePlural,
            onValueChange = {
                namePlural = it
                // When a name plural was manually set or edited, disable autofill plural
                autoFillPlural = false
            },
            label = {
                Text(stringResource(R.string.xently_text_field_label_name_plural))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddProductNamePagePreview() {
    XentlyTheme {
        AddProductNamePage(
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            suggestionsState = MutableStateFlow(emptyList()),
            search = {},
            saveDraft = {},
            onPreviousClick = {},
            onContinueClick = {},
            onSearchSuggestionSelected = {},
        )
    }
}