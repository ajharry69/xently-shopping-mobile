package ke.co.xently.shopping.features.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.ui.theme.XentlyTheme


@Composable
fun MultiStepNavigationButtons(
    modifier: Modifier = Modifier,
    buttons: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Divider()
        Row(
            content = buttons,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun MultiStepNavigationButtonsPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        MultiStepNavigationButtons {
            Button(onClick = { }) {
                Text(text = "Example")
            }
            Button(onClick = { }) {
                Text(text = "Another example")
            }
        }
    }
}