package ke.co.xently.products.ui.subscreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import ke.co.xently.products.models.ProductName

@Composable
fun AddProductNameScreen(modifier: Modifier = Modifier, onSaveClick: (ProductName) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    Column(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Add product name")
            TextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text("Name")
                },
                singleLine = true,
            )
        }

        Column(modifier = Modifier.wrapContentHeight()) {
            Divider()
            Button(
                onClick = {
                    ProductName.LocalViewModel.default.copy(
                        name = name.text,
                    ).let(onSaveClick)
                },
            ) {
                Text("Save & Continue")
            }
        }
    }
}