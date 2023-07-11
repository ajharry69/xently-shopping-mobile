package ke.co.xently.features.recommendations.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.ui.theme.XentlyTheme

enum class RecommendationResponseSortBy(@StringRes val label: Int) {
    Default(R.string.xently_recommendation_Response_sort_by_default),
    StoreName(R.string.xently_recommendation_Response_sort_by_store_name),
    StoreDistance(R.string.xently_recommendation_Response_sort_by_store_distance),
    EstimatedExpenditure(R.string.xently_recommendation_Response_sort_by_estimated_expenditure),
    HitCount(R.string.xently_recommendation_Response_sort_by_hit_count),
    MissCount(R.string.xently_recommendation_Response_sort_by_miss_count),
}

@Composable
fun RecommendationResponseSortOptions(
    sortBy: RecommendationResponseSortBy,
    onClick: (RecommendationResponseSortBy) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.xently_content_description_sort_recommendations_by),
                    fontWeight = FontWeight.Bold,
                )
            },
        )
        Divider()

        LazyColumn {
            items(RecommendationResponseSortBy.values(), key = { it }) {
                Surface(selected = sortBy == it, onClick = { onClick(it) }) {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(it.label))
                        },
                        leadingContent = {
                            RadioButton(selected = sortBy == it, onClick = { onClick(it) })
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationResponseSortOptionsPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        var sortBy by remember {
            mutableStateOf(RecommendationResponseSortBy.Default)
        }
        RecommendationResponseSortOptions(
            sortBy = sortBy,
            onClick = {
                sortBy = it
            },
        )
    }
}