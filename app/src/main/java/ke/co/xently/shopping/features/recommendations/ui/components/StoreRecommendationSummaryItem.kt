package ke.co.xently.shopping.features.recommendations.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import java.math.BigDecimal
import kotlin.random.Random

@Composable
fun StoreRecommendationSummaryItem(
    response: Recommendation.Response,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    ListItem(
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val isRedacted = response.store.isRedacted()
                Text(
                    text = if (isRedacted) {
                        stringResource(R.string.xently_text_label_store_name_redacted)
                    } else {
                        response.store.toString()
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isRedacted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                )
                trailingContent?.invoke()
            }
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun StoreRecommendationSummaryItemPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
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
                    unit = Random.nextInt(1000, 5000).toDouble(),
                    total = Random.nextInt(1500, 10000).toDouble(),
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
        StoreRecommendationSummaryItem(response = response)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun StoreRecommendationSummaryItemWithTrailingContentPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
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
                    unit = Random.nextInt(1000, 5000).toDouble(),
                    total = Random.nextInt(1500, 10000).toDouble(),
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
        StoreRecommendationSummaryItem(
            response = response,
            trailingContent = {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.clickable { },
                )
            },
        )
    }
}