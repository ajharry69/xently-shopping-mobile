package ke.co.xently.shopping.features.measurementunit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import ke.co.xently.shopping.features.measurementunit.ui.MeasurementUnitQuantityUIState


@Composable
internal fun MeasurementUnitQuantityTextField(
    value: TextFieldValue,
    uiState: MeasurementUnitQuantityUIState,
    measurementUnitName: String?,
    label: String,
    imeAction: ImeAction = ImeAction.Next,
    isError: () -> Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    supportingText: (@Composable () -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        isError = isError(),
        supportingText = if (isError()) {
            {
                Text(text = uiState(context = LocalContext.current))
            }
        } else supportingText,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
        suffix = if (measurementUnitName == null) {
            null
        } else {
            {
                Text(text = measurementUnitName)
            }
        },
    )
}