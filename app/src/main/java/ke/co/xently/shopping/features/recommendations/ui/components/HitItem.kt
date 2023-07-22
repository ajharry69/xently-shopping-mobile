package ke.co.xently.shopping.features.recommendations.ui.components

import android.content.res.Configuration
import android.icu.math.BigDecimal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.toSystemDefaultZonedDateTime
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.ui.theme.XentlyTheme
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.random.Random

@Composable
fun HitItem(item: Recommendation.Response.Hit.Item) {
    ListItem(
        headlineContent = {
            Text(text = item.shoppingList.name)
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = item.bestMatched.name)
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.xently_recommendation_response_approximate_unit_selling_price,
                        LocalContext.current.currencyNumberFormat.format(item.bestMatched.unitPrice),
                        LocalDateTime.parse(
                            item.bestMatched.latestDateOfPurchaseUTCString,
                            Recommendation.Response.Hit.Item.BestMatched.LATEST_DATE_OF_PURCHASE_FORMAT,
                        ).toSystemDefaultZonedDateTime(ZoneOffset.UTC)
                            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                    ),
                )
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.shoppingList.quantityToPurchase.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(Icons.Default.NavigateNext, contentDescription = null)
            }
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun HitItemPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        val item = Recommendation.Response.Hit.Item.default.run {
            copy(
                bestMatched = bestMatched.copy(
                    name = "Recorded product name #".plus(Random.nextInt(1, 10)),
                    unitPrice = BigDecimal.valueOf(Random.nextLong(10, 1000)),
                    totalPrice = BigDecimal.valueOf(Random.nextLong(1000, 5000)),
                ),
                shoppingList = shoppingList.copy(
                    name = "Requested product name #".plus(Random.nextInt(1, 10)),
                )
            )
        }
        HitItem(item = item)
    }
}