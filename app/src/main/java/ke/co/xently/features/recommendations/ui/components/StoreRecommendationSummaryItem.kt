package ke.co.xently.features.recommendations.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.features.core.currencyNumberFormat
import ke.co.xently.features.recommendations.models.Recommendation

@Composable
fun StoreRecommendationSummaryItem(
    response: Recommendation.Response,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    ListItem(
        trailingContent = trailingContent,
        headlineContent = {
            Text(text = response.store.toString())
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                response.store.getDistanceForDisplay(context)?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = LocalContentColor.current.copy(alpha = 0.8f),
                        )
                        Text(
                            text = stringResource(
                                R.string.xently_recommendation_response_approximate_store_distance,
                                it,
                            ),
                            color = LocalContentColor.current.copy(alpha = 0.8f),
                        )
                    }
                }
                Text(
                    text = stringResource(
                        R.string.xently_recommendation_response_summary,
                        response.hit.count,
                        response.hit.count + response.miss.count,
                        context.currencyNumberFormat.format(response.estimatedExpenditure.total),
                    ),
                )
            }
        },
    )
}