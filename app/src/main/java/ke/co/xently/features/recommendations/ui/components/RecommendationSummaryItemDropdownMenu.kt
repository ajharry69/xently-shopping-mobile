package ke.co.xently.features.recommendations.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.ui.theme.XentlyTheme

@Composable
fun RecommendationSummaryItemDropdownMenu(
    response: Recommendation.Response,
    showComparisonListItemMenu: Boolean,
    onNavigate: (Recommendation.Response) -> Unit,
    onViewProduct: ((Recommendation.Response) -> Unit)?,
    visitOnlineStore: (Recommendation.Response) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val onViewProductRemembered by rememberUpdatedState(onViewProduct)
    DropdownMenu(expanded = showComparisonListItemMenu, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.xently_navigate_to_store))
            },
            onClick = {
                onDismissRequest()
                onNavigate(response)
            },
            leadingIcon = {
                Icon(Icons.Default.NearMe, contentDescription = null)
            },
        )
        AnimatedVisibility(visible = onViewProductRemembered != null) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.xently_view_details))
                },
                onClick = {
                    onDismissRequest()
                    onViewProductRemembered?.invoke(response)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.FormatAlignLeft,
                        contentDescription = null
                    )
                },
            )
        }
        AnimatedVisibility(visible = response.hasAnOnlineStore()) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.xently_visit_online_store))
                },
                onClick = {
                    onDismissRequest()
                    visitOnlineStore(response)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null
                    )
                },
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationSummaryItemDropdownMenuPreview() {
    XentlyTheme {
        RecommendationSummaryItemDropdownMenu(
            response = Recommendation.Response.default,
            showComparisonListItemMenu = true,
            onNavigate = {},
            onViewProduct = {},
            visitOnlineStore = {},
            onDismissRequest = {},
        )
    }
}