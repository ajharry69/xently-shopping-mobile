package ke.co.xently.shopping.features.compareproducts.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.compareproducts.models.CompareProduct
import ke.co.xently.shopping.features.compareproducts.models.ComparisonListItem
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.ui.theme.XentlyTheme
import kotlin.random.Random

@Composable
fun CompareProductResponseScreen(
    modifier: Modifier,
    response: CompareProduct.Response,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.xently_compare_product_response_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            supportingContent = {
                val label by remember(response.orderedBy) {
                    derivedStateOf {
                        when (response.orderedBy) {
                            OrderBy.Ascending -> R.string.xently_compare_product_response_sort_by_cheapest_first
                            OrderBy.Descending -> R.string.xently_compare_product_response_sort_by_expensive_first
                        }
                    }
                }
                Text(
                    text = stringResource(
                        R.string.xently_compare_product_response_subtitle,
                        stringResource(label).toLowerCase(Locale.current),
                    ),
                )
            },
        )
        Divider()
        LazyColumn(modifier = Modifier.then(modifier)) {
            items(response.comparisonList, key = { it }) {
                ListItem(
                    headlineContent = {
                        Text(text = it.name)
                    },
                    supportingContent = {
                        Text(text = LocalContext.current.currencyNumberFormat.format(it.unitPrice))
                    },
                    trailingContent = {
                        // This is decorative
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    },
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun CompareProductResponseScreenPreview() {
    XentlyTheme {
        val comparisonList = List(Random.nextInt(2, 10)) {
            ComparisonListItem.default.copy(
                name = "Random ".plus(it + 1),
                unitPrice = Random.nextInt(100, 1000),
            )
        }
        CompareProductResponseScreen(
            modifier = Modifier.fillMaxSize(),
            response = CompareProduct.Response(OrderBy.Ascending, comparisonList),
        )
    }
}