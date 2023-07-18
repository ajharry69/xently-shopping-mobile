package ke.co.xently.features.core.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.features.core.ui.components.AutoCompleteSearchResults
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.launch
import kotlin.random.Random


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
    service: AutoCompleteService<Q, R> = AutoCompleteService.Fake(),
    isError: Boolean = false,
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
    val suggestions = remember {
        mutableStateListOf<R>()
    }

    AutoCompleteSearchResults(
        service = service,
        shouldShowSuggestions = {
            state.query.isNotBlank()
        },
        suggestions = {
            suggestions.clear()
            suggestions.addAll(it)
        },
    )

    var nameSearchActive by remember {
        mutableStateOf(false)
    }
    var wasSuggestionSelected by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state.query, suggestions) {
        if (wasSuggestionSelected) {
            wasSuggestionSelected = false
        } else {
            nameSearchActive = state.query.isNotBlank() && suggestions.isNotEmpty()
        }
    }
    val coroutineScope = rememberCoroutineScope()
    ke.co.xently.features.core.ui.autocomplete.AutoCompleteTextField(
        query = state.query,
        onQueryChange = {
            state.updateQuery(it)
            val query = onSearch(it)
            coroutineScope.launch {
                service.search(query, numberOfResults)
            }
        },
        isError = isError,
        active = nameSearchActive,
        onActiveChange = {},
        modifier = Modifier.then(modifier),
        label = label,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    ) {
        for (suggestion in suggestions) {
            ListItem(
                headlineContent = {
                    suggestionContent(suggestion)
                },
                modifier = Modifier.clickable {
                    onSuggestionSelected(suggestion)
                    wasSuggestionSelected = true
                    nameSearchActive = false
                    onSearchSuggestionSelected()
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
            AutoCompleteTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberAutoCompleteTextFieldState(),
                service = AutoCompleteService.Fake(suggestions),
                onSearch = { "" },
                onSuggestionSelected = {},
                onSearchSuggestionSelected = {},
                label = {
                    Text(text = "Search...")
                },
                suggestionContent = {
                    Text(text = it)
                },
            )
        }
    }
}