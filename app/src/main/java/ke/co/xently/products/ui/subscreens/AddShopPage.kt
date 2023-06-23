package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.R
import ke.co.xently.products.models.Shop
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.products.ui.components.AutoCompleteTextField
import ke.co.xently.products.ui.components.rememberAutoCompleteTextFieldState
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private sealed interface ShopContinueButtonLabel {
    @get:StringRes
    val label: Int

    object Continue : ShopContinueButtonLabel {
        override val label: Int = R.string.xently_button_label_continue
    }

    object MissingShopName : ShopContinueButtonLabel {
        override val label: Int = R.string.xently_button_label_missing_shop_name
    }
}

@Composable
fun AddShopPage(
    modifier: Modifier = Modifier,
    shop: Shop,
    suggestionsState: StateFlow<List<Shop>>,
    search: (Shop) -> Unit,
    saveDraft: (Shop) -> Unit,
    onSearchSuggestionSelected: () -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (Shop) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = shop.name,
        suggestionsState = suggestionsState,
    )
    var eCommerceWebsiteUrl by remember(shop.ecommerceSiteUrl) {
        mutableStateOf(TextFieldValue(shop.ecommerceSiteUrl ?: ""))
    }

    var buttonLabel by remember {
        mutableStateOf<ShopContinueButtonLabel>(ShopContinueButtonLabel.Continue)
    }

    LaunchedEffect(nameAutoCompleteState.query) {
        buttonLabel = if (nameAutoCompleteState.query.isBlank()) {
            ShopContinueButtonLabel.MissingShopName
        } else {
            ShopContinueButtonLabel.Continue
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_shop_page_title,
        subHeading = R.string.xently_add_shop_page_sub_heading,
        onBackClick = onPreviousClick,
        continueButton = {
            val enabled by remember(buttonLabel) {
                derivedStateOf {
                    buttonLabel is ShopContinueButtonLabel.Continue
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
                Text(stringResource(buttonLabel.label))
            }
        },
    ) {
        AutoCompleteTextField(
            modifier = Modifier.fillMaxWidth(),
            state = nameAutoCompleteState,
            onSearch = { query ->
                Shop.LocalViewModel.default.copy(name = query)
                    .let(search)
            },
            saveDraft = saveDraft,
            onSearchSuggestionSelected = onSearchSuggestionSelected,
            suggestionContent = { Text(text = it.name) },
            placeholderContent = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name_required))
            },
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
            suggestionsState = MutableStateFlow(emptyList()),
            search = {},
            saveDraft = {},
            onPreviousClick = {},
            onContinueClick = {},
            onSearchSuggestionSelected = {},
        )
    }
}