package ke.co.xently.features.recommendations.ui.components

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.ui.theme.XentlyTheme
import kotlin.random.Random


@Composable
fun MissItem(item: Recommendation.Response.Miss.Item) {
    ListItem(
        headlineContent = {
            Text(text = item.value)
        },
        trailingContent = {
            Icon(Icons.Default.NavigateNext, contentDescription = null)
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun MissItemPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        val item = Recommendation.Response.Miss.Item(
            value = "Product requested but not found #".plus(Random.nextInt(1, 10)),
        )
        MissItem(item = item)
    }
}