package ke.co.xently.products.ui.subscreens

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.xently.products.models.Store
import ke.co.xently.products.ui.components.AddProductPage

@Composable
fun AddStorePage(
    modifier: Modifier = Modifier,
    onContinueClick: (Store) -> Unit,
) {
    AddProductPage(
        modifier = modifier,
        buttons = {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    Store.LocalViewModel.default.let(onContinueClick)
                },
            ) {
                Text("Continue")
            }
        },
    ) {
        Text(text = "Store", style = MaterialTheme.typography.headlineMedium)
    }
}