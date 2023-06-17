package ke.co.xently.products.ui.subscreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.components.AddProductPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementUnitPage(
    modifier: Modifier = Modifier,
    product: Product,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    var unitQuantity by remember(product.measurementUnitQuantity) {
        mutableStateOf(TextFieldValue(product.measurementUnitQuantity.toString()))
    }
    var nameQuery by remember(product.measurementUnit?.name) {
        mutableStateOf(product.measurementUnit?.name ?: "")
    }

    val nameSearchActive by remember(nameQuery) {
        derivedStateOf {
            nameQuery.isNotBlank()
        }
    }

    var namePlural by remember(product.measurementUnit?.namePlural) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.namePlural ?: ""))
    }
    var symbol by remember(product.measurementUnit?.symbol) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.symbol ?: ""))
    }
    var symbolPlural by remember(product.measurementUnit?.symbolPlural) {
        mutableStateOf(TextFieldValue(product.measurementUnit?.symbolPlural ?: ""))
    }
    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_measurement_unit_page_title,
        subHeading = R.string.xently_measurement_unit_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    product.toLocalViewModel().copy(
                        measurementUnitQuantity = unitQuantity.text.toFloat(),
                        measurementUnit = MeasurementUnit.LocalViewModel.default.copy(
                            name = nameQuery,
                            namePlural = namePlural.text.takeIf { it.isNotBlank() },
                        ),
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        DockedSearchBar(
            query = nameQuery,
            onQueryChange = {
                nameQuery = it
            },
            onSearch = {
                // TODO: Trigger search
            },
            active = nameSearchActive,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
            placeholder = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
        ) {

        }
        TextField(
            value = namePlural,
            onValueChange = { namePlural = it },
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
        TextField(
            value = symbolPlural,
            onValueChange = { symbolPlural = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_symbol_plural))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = unitQuantity,
            onValueChange = { unitQuantity = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_unit_quantity))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        )
    }
}