package ke.co.xently.products.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddProductPage(
    modifier: Modifier = Modifier,
    buttons: @Composable RowScope.() -> Unit,
    form: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        Column(
            content = form,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )

        AddProductNavigationButtons(
            buttons = buttons,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}