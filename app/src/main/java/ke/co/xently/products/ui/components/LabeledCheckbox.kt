package ke.co.xently.products.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.ui.theme.XentlyTheme
import kotlin.random.Random


@Composable
fun LabeledCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: @Composable RowScope.() -> Unit,
) {
    Surface(
        checked = checked,
        onCheckedChange = onCheckedChange,
    ) {
        Row(
            modifier = Modifier.then(modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            label()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun LabeledCheckboxPreview() {
    XentlyTheme {
        Column {
            var checked by remember {
                mutableStateOf(Random.nextBoolean())
            }
            LabeledCheckbox(
                checked = Random.nextBoolean(),
                onCheckedChange = { checked = it },
            ) {
                Text(text = "Label")
            }
        }
    }
}