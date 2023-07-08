package ke.co.xently.features.recommendations.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.ui.components.RecommendationSummaryItemDropdownMenu
import ke.co.xently.features.recommendations.ui.components.StoreRecommendationSummaryItem
import ke.co.xently.ui.theme.XentlyTheme
import kotlin.random.Random

@Composable
fun RecommendationResponseScreen(
    modifier: Modifier,
    response: List<Recommendation.Response>,
    onNavigate: (Recommendation.Response) -> Unit,
    onViewProduct: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
) {
    LazyColumn(modifier = Modifier.then(modifier)) {
        items(response, key = { it.store.id }) { response ->
            Surface(onClick = { onViewProduct(response) }) {
                StoreRecommendationSummaryItem(response = response) {
                    var showComparisonListItemMenu by remember {
                        mutableStateOf(false)
                    }
                    Box {
                        IconButton(
                            onClick = {
                                showComparisonListItemMenu = !showComparisonListItemMenu
                            },
                        ) {
                            AnimatedContent(showComparisonListItemMenu) {
                                if (it) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(
                                            R.string.xently_content_description_options_for_item_reversed,
                                            response.store,
                                        ),
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = stringResource(
                                            R.string.xently_content_description_options_for_item,
                                            response.store,
                                        ),
                                    )
                                }
                            }
                        }
                        RecommendationSummaryItemDropdownMenu(
                            response = response,
                            showComparisonListItemMenu = showComparisonListItemMenu,
                            onNavigate = onNavigate,
                            onViewProduct = onViewProduct,
                            visitOnlineStore = visitOnlineStore,
                            onDismissRequest = {
                                showComparisonListItemMenu = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationResponseScreenPreview() {
    XentlyTheme {
        val responseList = List(Random.nextInt(2, 10)) {
            Recommendation.Response.default.run {
                copy(
                    store = store.run {
                        copy(
                            id = (it + 1).toLong(),
                            name = "Store #".plus(Random.nextInt(1, 10)),
                            shop = shop.copy(name = "Shop #".plus(Random.nextInt(1, 10))),
                            distance = if ((it + 1) in arrayOf(1, 3, 7)) {
                                null
                            } else {
                                Random.nextDouble(50.0, 1000.0)
                            },
                        )
                    },
                    estimatedExpenditure = Recommendation.Response.EstimatedExpenditure.default.copy(
                        unit = Random.nextInt(1000, 5000),
                        total = Random.nextInt(1500, 10000),
                    ),
                    hit = Recommendation.Response.Hit.default.copy(count = Random.nextInt(5)),
                    miss = Recommendation.Response.Miss.default.copy(count = Random.nextInt(3)),
                )
            }
        }
        RecommendationResponseScreen(
            modifier = Modifier.fillMaxSize(),
            response = responseList,
            onNavigate = {},
            onViewProduct = {},
            visitOnlineStore = {},
        )
    }
}