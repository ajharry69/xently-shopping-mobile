package ke.co.xently.shopping.features.products.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextField
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextFieldState
import ke.co.xently.shopping.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.shopping.features.products.ui.LocalAddProductStep
import ke.co.xently.shopping.remotedatasource.services.AutoCompleteService


@Composable
fun <Q, R> AddProductAutoCompleteTextField(
    modifier: Modifier = Modifier,
    service: AutoCompleteService<Q> = AutoCompleteService.Fake(),
    isError: Boolean = false,
    allowImojis: Boolean = false,
    numberOfResults: Int = 5,
    state: AutoCompleteTextFieldState = rememberAutoCompleteTextFieldState(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onSearch: (String) -> Q,
    onSuggestionSelected: (R) -> Unit,
    onSearchSuggestionSelected: () -> Unit = {},
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    suggestionContent: @Composable (R) -> Unit,
) {
    AutoCompleteTextField(
        modifier = modifier,
        service = service,
        isError = isError,
        allowImojis = allowImojis,
        numberOfResults = numberOfResults,
        state = state,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onSearch = onSearch,
        onSuggestionSelected = onSuggestionSelected,
        onSearchSuggestionSelected = onSearchSuggestionSelected,
        label = label,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        suggestionContent = suggestionContent,
        closeSessionKey = { LocalAddProductStep.current },
    )
}