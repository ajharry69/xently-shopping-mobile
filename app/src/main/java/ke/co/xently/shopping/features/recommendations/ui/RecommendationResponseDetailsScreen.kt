package ke.co.xently.shopping.features.recommendations.ui

import android.content.res.Configuration
import android.icu.math.BigDecimal
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.ui.components.HitItem
import ke.co.xently.shopping.features.recommendations.ui.components.MissItem
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationSummaryItemDropdownMenu
import ke.co.xently.shopping.features.recommendations.ui.components.StoreRecommendationSummaryItem
import ke.co.xently.shopping.ui.theme.XentlyTheme
import kotlin.random.Random

@Composable
fun RecommendationResponseDetailsScreen(
    modifier: Modifier,
    response: Recommendation.Response,
    onNavigate: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StoreRecommendationSummaryItem(response = response) {
            RecommendationSummaryItemTrailingContent(
                response = response,
                onNavigate = onNavigate,
                visitOnlineStore = visitOnlineStore,
            )
        }

        Divider()

        val areHitsEmpty by remember {
            derivedStateOf {
                response.hit.items.isEmpty()
            }
        }

        val areMissesEmpty by remember {
            derivedStateOf {
                response.miss.items.isEmpty()
            }
        }

        AnimatedContent(targetState = areHitsEmpty && areMissesEmpty) { areHitsAndMissesEmpty ->
            if (areHitsAndMissesEmpty) {
                Box(modifier = Modifier.then(modifier), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.xently_empty_recommendation_details))
                }
            } else {
                LazyColumn(modifier = Modifier.then(modifier)) {
                    if (!areHitsEmpty) {
                        item {
                            TitleText(title = stringResource(R.string.xently_hits_title))
                        }
                        items(
                            response.hit.items,
                            key = { it.bestMatched.name + it.shoppingList.name },
                        ) {
                            HitItem(item = it)
                        }
                    }
                    if (!areMissesEmpty) {
                        item {
                            TitleText(title = stringResource(R.string.xently_misses_title))
                        }
                        items(response.miss.items, key = { it.value }) {
                            MissItem(item = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RecommendationSummaryItemTrailingContent(
    response: Recommendation.Response,
    onNavigate: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit
) {
    AnimatedContent(targetState = response.hasAnOnlineStore()) { hasAnOnlineStore ->
        if (hasAnOnlineStore) {
            var showComparisonListItemMenu by remember {
                mutableStateOf(false)
            }
            Box {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        R.string.xently_content_description_options_for_item,
                        response.store,
                    ),
                    modifier = Modifier.clickable {
                        showComparisonListItemMenu = !showComparisonListItemMenu
                    },
                )

                RecommendationSummaryItemDropdownMenu(
                    response = response,
                    showComparisonListItemMenu = showComparisonListItemMenu,
                    onNavigate = onNavigate,
                    onViewProduct = null,
                    visitOnlineStore = visitOnlineStore,
                    onDismissRequest = {
                        showComparisonListItemMenu = false
                    },
                )
            }
        } else {
            PlainTooltipBox(
                tooltip = {
                    Text(text = stringResource(R.string.xently_navigate_to_store))
                },
            ) {
                Icon(
                    Icons.Default.NearMe,
                    contentDescription = stringResource(R.string.xently_navigate_to_store),
                    modifier = Modifier
                        .tooltipTrigger()
                        .clickable { onNavigate(response) },
                )
            }
        }
    }
}

@Composable
private fun TitleText(title: String) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationResponseDetailsScreenPreview() {
    XentlyTheme {
        val response = Recommendation.Response.default.run {
            copy(
                store = store.run {
                    copy(
                        id = 1,
                        name = "Store #".plus(Random.nextInt(1, 10)),
                        shop = shop.copy(name = "Shop #".plus(Random.nextInt(1, 10))),
                        distance = Random.nextDouble(50.0, 1000.0),
                    )
                },
                estimatedExpenditure = Recommendation.Response.EstimatedExpenditure.default.copy(
                    unit = Random.nextInt(1000, 5000),
                    total = Random.nextInt(1500, 10000),
                ),
                hit = Recommendation.Response.Hit.default.copy(count = Random.nextInt(5)).run {
                    val items = List(count) {
                        Recommendation.Response.Hit.Item.default.run {
                            copy(
                                bestMatched = bestMatched.copy(
                                    name = "Recorded product name #".plus(
                                        it + Random.nextInt(
                                            1,
                                            10
                                        )
                                    ),
                                    unitPrice = BigDecimal.valueOf(Random.nextLong(10, 1000)),
                                    totalPrice = BigDecimal.valueOf(Random.nextLong(1000, 5000)),
                                ),
                                shoppingList = shoppingList.copy(
                                    name = "Requested product name #".plus(
                                        it + Random.nextInt(
                                            1,
                                            10
                                        )
                                    ),
                                )
                            )
                        }
                    }
                    copy(items = items)
                },
                miss = Recommendation.Response.Miss.default.copy(count = Random.nextInt(3)).run {
                    val items = List(count) {
                        Recommendation.Response.Miss.Item(
                            value = "Product requested but not found #".plus(
                                it + Random.nextInt(
                                    1,
                                    10
                                )
                            ),
                        )
                    }
                    copy(items = items)
                },
            )
        }
        RecommendationResponseDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            response = response,
            onNavigate = {},
            visitOnlineStore = {},
        )
    }
}