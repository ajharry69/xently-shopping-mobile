package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.products.models.Shop
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.ui.theme.XentlyTheme

@Composable
fun AddShopPage(
    modifier: Modifier = Modifier,
    shop: Shop,
    onPreviousClick: () -> Unit,
    onContinueClick: (Shop) -> Unit,
) {
    var name by remember(shop.name) { mutableStateOf(TextFieldValue(shop.name)) }
    var eCommerceWebsiteUrl by remember(shop.ecommerceSiteUrl) {
        mutableStateOf(TextFieldValue(shop.ecommerceSiteUrl ?: ""))
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_shop_page_title,
        subHeading = R.string.xently_add_shop_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    shop.toLocalViewModel().copy(
                        name = name.text,
                        ecommerceSiteUrl = eCommerceWebsiteUrl.text.takeIf { it.isNotBlank() },
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_name_required))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            value = eCommerceWebsiteUrl,
            onValueChange = { eCommerceWebsiteUrl = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_ecommerce_website_url))
            },
            supportingText = {
                Text(text = stringResource(R.string.xently_text_field_hint_optional_ecommerce_site_url))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AddShopPagePreview() {
    XentlyTheme {
        AddShopPage(
            modifier = Modifier.fillMaxSize(),
            shop = Shop.LocalViewModel.default,
            onPreviousClick = {},
            onContinueClick = {},
        )
    }
}