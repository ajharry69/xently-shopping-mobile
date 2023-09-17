package ke.co.xently.shopping.features.measurementunit.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.hasEmojis
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextField
import ke.co.xently.shopping.features.core.ui.LabeledCheckbox
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
import ke.co.xently.shopping.features.products.models.Product

@Composable
fun AddMeasurementUnitNamePage(
    modifier: Modifier,
    product: Product,
    onSuggestionSelected: (MeasurementUnit) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = product.measurementUnit?.name
            ?: "",
    )

    var namePlural by remember(product.measurementUnit?.plural) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.plural ?: ""))
    }
    var symbol by remember(product.measurementUnit?.symbol) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.symbol ?: ""))
    }

    var autoFillMeasurementUnitNamePlural by rememberSaveable(product.toLocalViewModel().autoFillMeasurementUnitNamePlural) {
        mutableStateOf(product.toLocalViewModel().autoFillMeasurementUnitNamePlural)
    }

    LaunchedEffect(nameAutoCompleteState.query) {
        if (autoFillMeasurementUnitNamePlural) {
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

    val uiState by produceState<MeasurementUnitNameUIState>(
        MeasurementUnitNameUIState.OK,
        namePlural.text,
        symbol.text,
    ) {
        value = when {
            namePlural.text.hasEmojis -> {
                MeasurementUnitNameUIState.NamePluralError.ImojiNotAllowedError
            }

            symbol.text.hasEmojis -> {
                MeasurementUnitNameUIState.SymbolError.ImojiNotAllowedError
            }

            else -> {
                MeasurementUnitNameUIState.OK
            }
        }
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_measurement_unit_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState is MeasurementUnitNameUIState.OK,
                onClick = {
                    product.toLocalViewModel().run {
                        val unit = nameAutoCompleteState.query.takeIf { it.isNotBlank() }?.let {
                            (measurementUnit ?: MeasurementUnit.LocalViewModel.default)
                                .copy(name = it)
                        } ?: measurementUnit

                        copy(
                            autoFillMeasurementUnitNamePlural = autoFillMeasurementUnitNamePlural,
                            autoFillMeasurementUnitSymbolPlural = autoFillMeasurementUnitSymbolPlural,
                            measurementUnit = unit?.copy(
                                plural = namePlural.text.trim().toLowerCase(Locale.current)
                                    .takeIf { it.isNotBlank() },
                                symbol = symbol.text.trim().toLowerCase(Locale.current)
                                    .takeIf { it.isNotBlank() },
                            ),
                        )
                    }.let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        AutoCompleteTextField<MeasurementUnit, MeasurementUnit>(
            modifier = Modifier.fillMaxWidth(),
            service = LocalMeasurementUnitAutoCompleteService.current,
            state = nameAutoCompleteState,
            onSearch = { query ->
                MeasurementUnit.LocalViewModel.default.copy(name = query)
            },
            onSuggestionSelected = onSuggestionSelected,
            suggestionContent = {
                Text(text = it.toString())
            },
            label = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
        )

        LabeledCheckbox(
            checked = autoFillMeasurementUnitNamePlural,
            onCheckedChange = { autoFillMeasurementUnitNamePlural = it },
        ) {
            Text(text = stringResource(R.string.xently_checkbox_label_autofill_unit_name_plural))
        }

        TextField(
            value = namePlural,
            onValueChange = {
                namePlural = it
                // When a name plural was manually set or edited, disable autofill plural
                autoFillMeasurementUnitNamePlural = false
            },
            label = {
                Text(stringResource(R.string.xently_text_field_label_name_plural))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState is MeasurementUnitNameUIState.NamePluralError,
            supportingText = if (uiState is MeasurementUnitNameUIState.NamePluralError) {
                {
                    Text(text = uiState(context = LocalContext.current))
                }
            } else null,
        )
        TextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_symbol))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState is MeasurementUnitNameUIState.SymbolError,
            supportingText = if (uiState is MeasurementUnitNameUIState.SymbolError) {
                {
                    Text(text = uiState(context = LocalContext.current))
                }
            } else null,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddMeasurementUnitNamePagePreview() {
    XentlyTheme {
        AddMeasurementUnitNamePage(
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            onSuggestionSelected = {},
            onPreviousClick = {},
        ) {}
    }
}