package ke.co.xently.features.products.ui.subscreens

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.features.core.ui.AutoCompleteTextField
import ke.co.xently.features.core.ui.LabeledCheckbox
import ke.co.xently.features.core.ui.MultiStepScreen
import ke.co.xently.features.core.ui.UIState
import ke.co.xently.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.features.products.datasources.remoteservices.ProductAutoCompleteService
import ke.co.xently.features.products.models.Product
import ke.co.xently.ui.theme.XentlyTheme

private sealed class ProductNameUIState(message: Int) : UIState(message) {
    object OK : ProductNameUIState(android.R.string.ok)

    object MissingProductName :
        ProductNameUIState(R.string.xently_button_label_missing_product_name)
}

@Composable
fun AddProductNamePage(
    modifier: Modifier,
    product: Product,
    service: ProductAutoCompleteService,
    saveDraft: (Product) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = product.name.name
    )

    var namePlural by remember(product.name.namePlural) {
        mutableStateOf(TextFieldValue(product.name.namePlural ?: ""))
    }

    var uiState by remember {
        mutableStateOf<ProductNameUIState>(ProductNameUIState.OK)
    }

    LaunchedEffect(nameAutoCompleteState.query) {
        uiState = if (nameAutoCompleteState.query.isBlank()) {
            ProductNameUIState.MissingProductName
        } else {
            ProductNameUIState.OK
        }
    }

    var autoFillPlural by rememberSaveable(product.toLocalViewModel().autoFillNamePlural) {
        mutableStateOf(product.toLocalViewModel().autoFillNamePlural)
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

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_product_name_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            val enabled by remember(uiState) {
                derivedStateOf {
                    uiState is ProductNameUIState.OK
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
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        AutoCompleteTextField(
            modifier = Modifier.fillMaxWidth(),
            service = service,
            state = nameAutoCompleteState,
            onSearch = { query ->
                Product.LocalViewModel.default.run {
                    copy(name = name.copy(name = query))
                }
            },
            onSuggestionSelected = saveDraft,
            suggestionContent = {
                Text(text = it.descriptiveName.ifBlank { it.name.name })
            },
            label = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name_required))
            },
            isError = uiState is ProductNameUIState.MissingProductName,
            supportingText = if (uiState is ProductNameUIState.MissingProductName) {
                {
                    Text(text = uiState(LocalContext.current))
                }
            } else null,
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
            service = ProductAutoCompleteService.Fake,
            saveDraft = {},
            onPreviousClick = {},
        ) {}
    }
}