package ke.co.xently.shopping.features.measurementunit.ui

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.cleansedForNumberParsing
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.products.models.Product

@Composable
fun AddMeasurementUnitQuantityPage(
    modifier: Modifier,
    product: Product,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val measurementUnit = product.measurementUnit
    var unitQuantity by remember(product.measurementUnitQuantity) {
        val quantity = product.measurementUnitQuantity.toString()
            .replace(".0", "")
        mutableStateOf(TextFieldValue(quantity))
    }

    var uiState by remember {
        mutableStateOf<MeasurementUnitQuantityUIState>(MeasurementUnitQuantityUIState.OK)
    }

    LaunchedEffect(unitQuantity.text) {
        uiState = when {
            unitQuantity.text.isNotBlank() && unitQuantity.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUnitQuantityUIState.QuantityError.InvalidQuantity
            }

            else -> {
                MeasurementUnitQuantityUIState.OK
            }
        }
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_measurement_unit_quantity_page_title,
        subheading = R.string.xently_measurement_unit_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState is MeasurementUnitQuantityUIState.OK,
                onClick = {
                    product.toLocalViewModel().run {
                        copy(
                            measurementUnitQuantity = unitQuantity.text
                                .cleansedForNumberParsing()
                                .toFloatOrNull() ?: 1f,
                        )
                    }.let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        TextField(
            value = unitQuantity,
            onValueChange = { unitQuantity = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_unit_quantity))
            },
            isError = uiState is MeasurementUnitQuantityUIState.QuantityError,
            supportingText = if (uiState is MeasurementUnitQuantityUIState.QuantityError) {
                {
                    Text(text = uiState(context = LocalContext.current))
                }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            suffix = if (measurementUnit == null) {
                null
            } else {
                {
                    Text(text = measurementUnit.name)
                }
            },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddMeasurementUnitQuantityPagePreview() {
    XentlyTheme {
        AddMeasurementUnitQuantityPage(
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            onPreviousClick = {},
        ) {}
    }
}