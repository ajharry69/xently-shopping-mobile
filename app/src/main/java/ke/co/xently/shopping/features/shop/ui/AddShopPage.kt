package ke.co.xently.shopping.features.shop.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextField
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.shop.models.Shop


@Composable
fun AddShopPage(
    modifier: Modifier = Modifier,
    shop: Shop,
    saveDraft: (Shop) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Shop) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = shop.name,
    )
    var eCommerceWebsiteUrl by remember(shop.ecommerceSiteUrl) {
        mutableStateOf(TextFieldValue(shop.ecommerceSiteUrl ?: ""))
    }

    val uiState by produceState<ShopUIState>(ShopUIState.OK, nameAutoCompleteState.query) {
        value = if (nameAutoCompleteState.query.isBlank()) {
            ShopUIState.MissingShopName
        } else {
            ShopUIState.OK
        }
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_add_shop_page_title,
        subheading = R.string.xently_add_shop_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            val enabled by remember(uiState) {
                derivedStateOf {
                    uiState is ShopUIState.OK
                }
            }
            Button(
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    shop.toLocalViewModel().copy(
                        name = nameAutoCompleteState.query,
                        ecommerceSiteUrl = eCommerceWebsiteUrl.text.takeIf { it.isNotBlank() },
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        AutoCompleteTextField<Shop, Shop>(
            modifier = Modifier.fillMaxWidth(),
            state = nameAutoCompleteState,
            service = LocalShopAutoCompleteService.current,
            onSearch = { query ->
                Shop.LocalViewModel.default.copy(name = query)
            },
            onSuggestionSelected = saveDraft,
            suggestionContent = { Text(text = it.name) },
            label = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name_required))
            },
            isError = uiState is ShopUIState.MissingShopName,
            supportingText = if (uiState is ShopUIState.MissingShopName) {
                {
                    Text(text = uiState(LocalContext.current))
                }
            } else null,
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddShopPagePreview() {
    XentlyTheme {
        AddShopPage(
            modifier = Modifier.fillMaxSize(),
            shop = Shop.LocalViewModel.default,
            saveDraft = {},
            onPreviousClick = {},
        ) {}
    }
}