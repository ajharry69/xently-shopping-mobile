package ke.co.xently.shopping.features.recommendations.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortBy
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortOptions
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationSummaryItemDropdownMenu
import ke.co.xently.shopping.features.recommendations.ui.components.StoreRecommendationSummaryItem
import ke.co.xently.shopping.ui.theme.XentlyTheme
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationResponseScreen(
    modifier: Modifier,
    responses: List<Recommendation.Response>,
    onNavigate: (Recommendation.Response) -> Unit,
    onViewProduct: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
) {
    var showSortByDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var sortBy by rememberSaveable {
        mutableStateOf(RecommendationResponseSortBy.Default)
    }

    var orderBy by rememberSaveable {
        mutableStateOf(OrderBy.Ascending)
    }

    val sortedResponses = remember(sortBy, orderBy) {
        val sortSelector: (Recommendation.Response) -> String? = {
            when (sortBy) {
                RecommendationResponseSortBy.Default -> null
                RecommendationResponseSortBy.StoreName -> it.store.name
                RecommendationResponseSortBy.StoreDistance -> it.store.distance?.toString()
                RecommendationResponseSortBy.HitCount -> it.hit.count.toString()
                RecommendationResponseSortBy.MissCount -> it.miss.count.toString()
                RecommendationResponseSortBy.EstimatedExpenditure -> it.estimatedExpenditure.total.toString()
            }
        }
        when (orderBy) {
            OrderBy.Ascending -> responses.sortedBy(sortSelector)
            OrderBy.Descending -> responses.sortedByDescending(sortSelector)
        }
    }

    AnimatedVisibility(visible = showSortByDialog) {
        AlertDialog(onDismissRequest = { showSortByDialog = false }) {
            RecommendationResponseSortOptions(sortBy) {
                sortBy = it
                showSortByDialog = false
            }
        }
    }

    Column {
        Column {
            ListItem(
                headlineContent = {
                    Row {
                        Text(
                            text = stringResource(R.string.xently_recommendations),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = responses.size.toString(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                supportingContent = {
                    Surface(
                        onClick = { showSortByDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = stringResource(R.string.xently_content_description_sort_recommendations),
                            )
                            Column {
                                Text(text = stringResource(sortBy.label))
                                PlainTooltipBox(
                                    tooltip = {
                                        Text(
                                            text = stringResource(
                                                R.string.xently_switch_order,
                                                stringResource(orderBy.opposite.label)
                                                    .toLowerCase(Locale.current),
                                            ),
                                        )
                                    },
                                ) {
                                    Text(
                                        text = stringResource(orderBy.label),
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier
                                            .tooltipTrigger()
                                            .toggleable(
                                                value = true,
                                                role = Role.Switch,
                                                onValueChange = {
                                                    orderBy = orderBy.opposite
                                                },
                                            ),
                                    )
                                }
                            }
                        }
                    }
                },
            )

            Divider()
        }

        LazyColumn(modifier = Modifier.then(modifier)) {
            items(sortedResponses, key = { it.store.id }) { response ->
                Surface(onClick = { onViewProduct(response) }) {
                    StoreRecommendationSummaryItem(response = response) {
                        var showComparisonListItemMenu by remember {
                            mutableStateOf(false)
                        }
                        Box {
                            val onClick by rememberUpdatedState {
                                showComparisonListItemMenu = !showComparisonListItemMenu
                            }
                            AnimatedContent(showComparisonListItemMenu) {
                                if (it) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(
                                            R.string.xently_content_description_options_for_item_reversed,
                                            response.store,
                                        ),
                                        modifier = Modifier.clickable(onClick = onClick),
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = stringResource(
                                            R.string.xently_content_description_options_for_item,
                                            response.store,
                                        ),
                                        modifier = Modifier.clickable(onClick = onClick),
                                    )
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
            responses = responseList,
            onNavigate = {},
            onViewProduct = {},
            visitOnlineStore = {},
        )
    }
}