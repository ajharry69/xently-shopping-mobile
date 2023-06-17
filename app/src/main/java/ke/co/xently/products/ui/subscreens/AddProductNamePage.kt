package ke.co.xently.products.ui.subscreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import ke.co.xently.products.models.ProductName
import ke.co.xently.products.ui.components.AddProductPage

@Composable
fun AddProductNamePage(
    modifier: Modifier = Modifier,
    productName: ProductName,
    onPreviousClick: () -> Unit,
    onContinueClick: (ProductName) -> Unit,
) {
    var name by remember(productName.name) { mutableStateOf(TextFieldValue(productName.name)) }
    var namePlural by remember(productName.namePlural) {
        mutableStateOf(TextFieldValue(productName.namePlural ?: ""))
    }
    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_product_name_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    ProductName.LocalViewModel.default.copy(
                        name = name.text,
                        namePlural = namePlural.text.takeIf { it.isNotBlank() },
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
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
    }
}