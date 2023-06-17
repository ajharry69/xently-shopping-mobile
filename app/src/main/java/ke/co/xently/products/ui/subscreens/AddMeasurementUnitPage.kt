package ke.co.xently.products.ui.subscreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import ke.co.xently.R
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.ui.components.AddProductPage

@Composable
fun AddMeasurementUnitPage(
    modifier: Modifier = Modifier,
    measurementUnit: MeasurementUnit?,
    onPreviousClick: () -> Unit,
    onContinueClick: (MeasurementUnit?) -> Unit,
) {
    var name by remember(measurementUnit?.name) {
        mutableStateOf(TextFieldValue(measurementUnit?.name ?: ""))
    }
    var namePlural by remember(measurementUnit?.namePlural) {
        mutableStateOf(TextFieldValue(measurementUnit?.namePlural ?: ""))
    }
    var symbol by remember(measurementUnit?.symbol) {
        mutableStateOf(TextFieldValue(measurementUnit?.symbol ?: ""))
    }
    var symbolPlural by remember(measurementUnit?.symbolPlural) {
        mutableStateOf(TextFieldValue(measurementUnit?.symbolPlural ?: ""))
    }
    AddProductPage(
        modifier = modifier,
        buttons = {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onPreviousClick,
            ) {
                Text(stringResource(R.string.xently_button_label_back))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    MeasurementUnit.LocalViewModel.default.copy(
                        name = name.text,
                        namePlural = namePlural.text.takeIf { it.isNotBlank() },
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        Text(
            text = stringResource(R.string.xently_measurement_unit_page_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_name_required))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
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
    }
}