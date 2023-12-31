package ke.co.xently.shopping.features.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.features.core.hasEmojis
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import kotlin.random.Random
import ke.co.xently.shopping.features.core.ui.autocomplete.AutoCompleteTextField as CoreAutoCompleteTextField


class AutoCompleteTextFieldState {
    var query by mutableStateOf("")
        private set

    fun updateQuery(query: String) {
        this.query = query
    }

    fun resetQuery() {
        this.query = ""
    }
}

@Composable
fun rememberAutoCompleteTextFieldState(
    query: String = "",
    vararg key: Any?,
): AutoCompleteTextFieldState {
    val state = remember(*key) {
        AutoCompleteTextFieldState()
    }

    LaunchedEffect(query) {
        state.updateQuery(query)
    }
    return state
}

@Composable
fun <Q, R> AutoCompleteTextField(
    modifier: Modifier = Modifier,
    service: AutoCompleteService<Q> = AutoCompleteService.Fake(),
    invalidateActiveSearch: Boolean = false,
    isError: Boolean = false,
    allowImojis: Boolean = false,
    numberOfResults: Int = 5,
    state: AutoCompleteTextFieldState = rememberAutoCompleteTextFieldState(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onSearch: (String) -> Q,
    onSuggestionSelected: (R) -> Unit,
    label: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    supportingText: @Composable() (() -> Unit)? = null,
    suggestionContent: @Composable (R) -> Unit,
) {
    val suggestions = remember {
        mutableStateListOf<R>()
    }

    val resultState: AutoCompleteService.ResultState by service.resultState
        .collectAsState(AutoCompleteService.ResultState.Idle)

    LaunchedEffect(resultState) {
        if (resultState is AutoCompleteService.ResultState.Failure) {
            suggestions.clear()
        } else if (resultState is AutoCompleteService.ResultState.Success<*>) {
            @Suppress("UNCHECKED_CAST")
            val results =
                (resultState as AutoCompleteService.ResultState.Success<*>).data as List<R>
            suggestions.clear()
            suggestions.addAll(results)
        }
    }

    val shouldReportEmojiProhibition by remember(allowImojis, state.query) {
        derivedStateOf {
            !allowImojis && state.query.hasEmojis
        }
    }

    var searchActive by remember(invalidateActiveSearch) {
        mutableStateOf(false)
    }
    var wasSuggestionSelected by remember {
        mutableStateOf(false)
    }

    val hasErrors by remember(isError, shouldReportEmojiProhibition) {
        derivedStateOf {
            isError || shouldReportEmojiProhibition
        }
    }

    LaunchedEffect(state.query, suggestions, hasErrors) {
        if (wasSuggestionSelected || hasErrors) {
            wasSuggestionSelected = false
            searchActive = false
        } else {
            (state.query.isNotBlank() && suggestions.isNotEmpty()).let {
                searchActive = it
            }
        }
    }

    val currentOnSearch by rememberUpdatedState(onSearch)
    if (!hasErrors) {
        LaunchedEffect(state.query, service, numberOfResults) {
            // No need to send an unnecessary search request if an error was encountered
            val query = currentOnSearch(state.query)
            service.search(query, numberOfResults)
        }
    }

    CoreAutoCompleteTextField(
        query = state.query,
        onQueryChange = state::updateQuery,
        isError = hasErrors,
        active = searchActive,
        onActiveChange = {},
        modifier = Modifier.then(modifier),
        label = label,
        trailingIcon = trailingIcon,
        supportingText = if (!shouldReportEmojiProhibition) supportingText else {
            {
                Text(text = stringResource(ke.co.xently.shopping.R.string.xently_error_imojis_not_allowed))
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    ) {
        val focusManager = LocalFocusManager.current
        for (suggestion in suggestions) {
            ListItem(
                headlineContent = {
                    suggestionContent(suggestion)
                },
                modifier = Modifier.clickable {
                    onSuggestionSelected(suggestion)
                    wasSuggestionSelected = true
                    searchActive = false
                    focusManager.clearFocus()
                },
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AutoCompleteTextFieldPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        val suggestions = List(Random.nextInt(0, 10)) {
            buildString {
                append("Suggestion ")
                append(it + 1)
            }
        }
        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            AutoCompleteTextField<String, String>(
                modifier = Modifier.fillMaxWidth(),
                service = AutoCompleteService.Fake(
                    AutoCompleteService.ResultState.Success(
                        suggestions
                    )
                ),
                state = rememberAutoCompleteTextFieldState(),
                onSearch = { "" },
                onSuggestionSelected = {},
                label = {
                    Text(text = "Search...")
                },
            ) {
                Text(text = it)
            }
        }
    }
}