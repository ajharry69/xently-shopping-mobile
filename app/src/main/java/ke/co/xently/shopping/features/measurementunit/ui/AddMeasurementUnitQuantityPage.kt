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
import ke.co.xently.shopping.features.core.toStringWithoutUnnecessaryDigitsAfterDecimalPoint
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
        val quantity = product.measurementUnitQuantity.standalone
            .toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        mutableStateOf(TextFieldValue(quantity))
    }
    var length by remember(product.measurementUnitQuantity.twoOrThreeDimension?.length) {
        val length = (product.measurementUnitQuantity.twoOrThreeDimension?.length ?: "").toString()
            .toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        mutableStateOf(TextFieldValue(length))
    }
    var width by remember(product.measurementUnitQuantity.twoOrThreeDimension?.width) {
        val width = (product.measurementUnitQuantity.twoOrThreeDimension?.width ?: "").toString()
            .toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        mutableStateOf(TextFieldValue(width))
    }
    var height by remember(product.measurementUnitQuantity.twoOrThreeDimension?.height) {
        val height = (product.measurementUnitQuantity.twoOrThreeDimension?.height ?: "").toString()
            .toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
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
                        val twoOrThreeDimension = if (hasValidValueForThreeDimension) {
                            (measurementUnitQuantity.twoOrThreeDimension
                                ?: MeasurementUnitQuantity.TwoOrThreeDimension.default).copy(
                                length = length.text.cleansedForNumberParsing().toFloatOrNull()
                                    ?: 1f,
                                width = width.text.cleansedForNumberParsing().toFloatOrNull() ?: 1f,
                                height = height.text.cleansedForNumberParsing().toFloatOrNull(),
                            )
                        } else {
                            null
                        }
                        copy(
                            measurementUnitQuantity = measurementUnitQuantity.copy(
                                standalone = standalone.text
                                    .cleansedForNumberParsing()
                                    .toFloatOrNull() ?: 1f,
                                twoOrThreeDimension = twoOrThreeDimension
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
            onValueChange = { standalone = it },
        )
        Text(text = stringResource(R.string.xently_subheading_multidimentional_measurement_unit_quantity))
        MeasurementUnitQuantityTextField(
            value = length,
            uiState = uiState,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_length),
            isError = { uiState is MeasurementUnitQuantityUIState.LengthError },
            onValueChange = { length = it },
        )
        MeasurementUnitQuantityTextField(
            value = width,
            uiState = uiState,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_width),
            isError = { uiState is MeasurementUnitQuantityUIState.WidthError },
            onValueChange = { width = it },
        )
        MeasurementUnitQuantityTextField(
            value = height,
            uiState = uiState,
            imeAction = ImeAction.Done,
            measurementUnitName = measurementUnit?.name,
            label = stringResource(R.string.xently_text_field_label_unit_quantity_height),
            isError = { uiState is MeasurementUnitQuantityUIState.HeightError },
            onValueChange = { height = it },
        ) {
            Text(text = stringResource(R.string.xently_help_text_height_field))
        }
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