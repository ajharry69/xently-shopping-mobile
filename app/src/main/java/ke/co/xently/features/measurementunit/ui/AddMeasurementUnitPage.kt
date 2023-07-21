package ke.co.xently.features.measurementunit.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.features.core.cleansedForNumberParsing
import ke.co.xently.features.core.ui.LabeledCheckbox
import ke.co.xently.features.core.ui.MultiStepScreen
import ke.co.xently.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.ui.components.AddProductAutoCompleteTextField
import ke.co.xently.ui.theme.XentlyTheme

private sealed interface MeasurementUIState {
    operator fun invoke(context: Context): String

    sealed interface QuantityError : MeasurementUIState

    object OK : MeasurementUIState {
        override operator fun invoke(context: Context): String {
            return context.getString(android.R.string.ok)
        }
    }

    object InvalidQuantity : QuantityError {
        override operator fun invoke(context: Context): String {
            return context.getString(R.string.xently_button_label_invalid_unit_quantity)
        }
    }
}

@Composable
fun AddMeasurementUnitPage(
    modifier: Modifier,
    product: Product,
    service: MeasurementUnitAutoCompleteService,
    onSuggestionSelected: (MeasurementUnit) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    var unitQuantity by remember(product.measurementUnitQuantity) {
        mutableStateOf(TextFieldValue(product.measurementUnitQuantity.toString()))
    }
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = product.measurementUnit?.name ?: ""
    )

    var namePlural by remember(product.measurementUnit?.namePlural) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.namePlural ?: ""))
    }
    var symbol by remember(product.measurementUnit?.symbol) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.symbol ?: ""))
    }
    var symbolPlural by remember(product.measurementUnit?.symbolPlural) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.symbolPlural ?: ""))
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

    var autoFillMeasurementUnitSymbolPlural by rememberSaveable(product.toLocalViewModel().autoFillMeasurementUnitSymbolPlural) {
        mutableStateOf(product.toLocalViewModel().autoFillMeasurementUnitSymbolPlural)
    }

    LaunchedEffect(symbol.text) {
        if (autoFillMeasurementUnitSymbolPlural) {
            symbolPlural = if (symbol.text.isBlank()) {
                TextFieldValue()
            } else {
                val plural = buildString {
                    append(symbol.text.trim())
                    append('s')
                }
                symbolPlural.copy(plural, selection = TextRange(plural.lastIndex))
            }
        }
    }

    var uiState by remember {
        mutableStateOf<MeasurementUIState>(MeasurementUIState.OK)
    }

    LaunchedEffect(unitQuantity.text) {
        uiState = when {
            unitQuantity.text.isNotBlank() && unitQuantity.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUIState.InvalidQuantity
            }

            else -> {
                MeasurementUIState.OK
            }
        }
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_measurement_unit_page_title,
        subheading = R.string.xently_measurement_unit_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState is MeasurementUIState.OK,
                onClick = {
                    product.toLocalViewModel().run {
                        val unit = nameAutoCompleteState.query.takeIf { it.isNotBlank() }?.let {
                            (measurementUnit ?: MeasurementUnit.LocalViewModel.default)
                                .copy(name = it)
                        } ?: measurementUnit

                        copy(
                            autoFillMeasurementUnitNamePlural = autoFillMeasurementUnitNamePlural,
                            autoFillMeasurementUnitSymbolPlural = autoFillMeasurementUnitSymbolPlural,
                            measurementUnitQuantity = unitQuantity.text
                                .cleansedForNumberParsing()
                                .toFloatOrNull() ?: 1f,
                            measurementUnit = unit?.copy(
                                namePlural = namePlural.text.trim().toLowerCase(Locale.current)
                                    .takeIf { it.isNotBlank() },
                                symbol = symbol.text.trim().toLowerCase(Locale.current)
                                    .takeIf { it.isNotBlank() },
                                symbolPlural = symbolPlural.text.trim().toLowerCase(Locale.current)
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
        AddProductAutoCompleteTextField<MeasurementUnit, MeasurementUnit>(
            modifier = Modifier.fillMaxWidth(),
            service = service,
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
        )
        TextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_symbol))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        LabeledCheckbox(
            checked = autoFillMeasurementUnitSymbolPlural,
            onCheckedChange = { autoFillMeasurementUnitSymbolPlural = it },
        ) {
            Text(text = stringResource(R.string.xently_checkbox_label_autofill_unit_symbol_plural))
        }

        TextField(
            value = symbolPlural,
            onValueChange = {
                symbolPlural = it
                // When a name plural was manually set or edited, disable autofill plural
                autoFillMeasurementUnitSymbolPlural = false
            },
            label = {
                Text(stringResource(R.string.xently_text_field_label_symbol_plural))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = unitQuantity,
            isError = uiState is MeasurementUIState.QuantityError,
            onValueChange = { unitQuantity = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_unit_quantity))
            },
            supportingText = if (uiState is MeasurementUIState.QuantityError) {
                {
                    Text(text = uiState(context = LocalContext.current))
                }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AddMeasurementUnitPagePreview() {
    XentlyTheme {
        AddMeasurementUnitPage(
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            service = MeasurementUnitAutoCompleteService.Fake,
            onSuggestionSelected = {},
            onPreviousClick = {},
        ) {}
    }
}