package ke.co.xently.shopping.features.measurementunit.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.cleansedForNumberParsing
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.measurementunit.ui.components.MeasurementUnitQuantityTextField
import ke.co.xently.shopping.features.products.models.MeasurementUnitQuantity
import ke.co.xently.shopping.features.products.models.Product

@Composable
fun AddMeasurementUnitQuantityPage(
    modifier: Modifier,
    product: Product,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val measurementUnit = product.measurementUnit
    var standalone by remember(product.measurementUnitQuantity.standalone) {
        val quantity = product.measurementUnitQuantity.standalone.toString()
            .replace(".0", "")
        mutableStateOf(TextFieldValue(quantity))
    }
    var length by remember(product.measurementUnitQuantity.threeDimension?.length) {
        val length = (product.measurementUnitQuantity.threeDimension?.length ?: "").toString()
            .replace(".0", "")
        mutableStateOf(TextFieldValue(length))
    }
    var width by remember(product.measurementUnitQuantity.threeDimension?.width) {
        val width = (product.measurementUnitQuantity.threeDimension?.width ?: "").toString()
            .replace(".0", "")
        mutableStateOf(TextFieldValue(width))
    }
    var height by remember(product.measurementUnitQuantity.threeDimension?.height) {
        val height = (product.measurementUnitQuantity.threeDimension?.height ?: "").toString()
            .replace(".0", "")
        mutableStateOf(TextFieldValue(height))
    }

    var uiState by remember {
        mutableStateOf<MeasurementUnitQuantityUIState>(MeasurementUnitQuantityUIState.OK)
    }

    LaunchedEffect(standalone.text) {
        uiState = when {
            standalone.text.isNotBlank() && standalone.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUnitQuantityUIState.StandaloneError.InvalidStandalone
            }

            length.text.isNotBlank() && length.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUnitQuantityUIState.LengthError.InvalidLength
            }

            width.text.isNotBlank() && width.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUnitQuantityUIState.WidthError.InvalidWidth
            }

            height.text.isNotBlank() && height.text.cleansedForNumberParsing()
                .toFloatOrNull() == null -> {
                MeasurementUnitQuantityUIState.WidthError.InvalidWidth
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
                        val hasValidValueForThreeDimension = listOf(length, width, height).any {
                            it.text.cleansedForNumberParsing().isNotBlank()
                        }
                        val threeDimension = if (hasValidValueForThreeDimension) {
                            (measurementUnitQuantity.threeDimension
                                ?: MeasurementUnitQuantity.ThreeDimension.default).copy(
                                length = length.text.cleansedForNumberParsing().toFloatOrNull()
                                    ?: 1f,
                                width = width.text.cleansedForNumberParsing().toFloatOrNull() ?: 1f,
                                height = height.text.cleansedForNumberParsing().toFloatOrNull()
                                    ?: 1f,
                            )
                        } else {
                            null
                        }
                        copy(
                            measurementUnitQuantity = measurementUnitQuantity.copy(
                                standalone = standalone.text
                                    .cleansedForNumberParsing()
                                    .toFloatOrNull() ?: 1f,
                                threeDimension = threeDimension
                            ),
                        )
                    }.let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        MeasurementUnitQuantityTextField(
            value = standalone,
            uiState = uiState,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_standalone),
            isError = { uiState is MeasurementUnitQuantityUIState.StandaloneError },
        ) { standalone = it }
        Text(text = "Please fill the following if the product is measured by the following dimensions?")
        MeasurementUnitQuantityTextField(
            value = length,
            uiState = uiState,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_length),
            isError = { uiState is MeasurementUnitQuantityUIState.LengthError },
        ) { length = it }
        MeasurementUnitQuantityTextField(
            value = width,
            uiState = uiState,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_width),
            isError = { uiState is MeasurementUnitQuantityUIState.WidthError },
        ) { width = it }
        MeasurementUnitQuantityTextField(
            value = height,
            uiState = uiState,
            imeAction = ImeAction.Done,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_height),
            isError = { uiState is MeasurementUnitQuantityUIState.HeightError },
        ) { height = it }
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