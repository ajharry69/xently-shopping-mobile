package ke.co.xently.products.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random


class AutoCompleteTextFieldState<T>(val suggestionsState: StateFlow<List<T>>) {
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
fun <T> rememberAutoCompleteTextFieldState(
    suggestionsState: StateFlow<List<T>>,
    query: String = "",
    vararg key: Any?,
): AutoCompleteTextFieldState<T> {
    val state = remember(*key) {
        AutoCompleteTextFieldState(suggestionsState)
    }

    LaunchedEffect(query) {
        state.updateQuery(query)
    }
    return state
}

@Composable
fun <T> rememberAutoCompleteTextFieldState(
    suggestions: List<T> = emptyList(),
    query: String = "",
    vararg key: Any?,
): AutoCompleteTextFieldState<T> {
    return rememberAutoCompleteTextFieldState(
        query = query,
        suggestionsState = MutableStateFlow(suggestions),
        key = key,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> AutoCompleteTextField(
    modifier: Modifier = Modifier,
    state: AutoCompleteTextFieldState<T> = rememberAutoCompleteTextFieldState(emptyList()),
    onSearch: (String) -> Unit,
    onSuggestionSelected: (T) -> Unit,
    onSearchSuggestionSelected: () -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    suggestionContent: @Composable (T) -> Unit,
) {
    val suggestions by state.suggestionsState.collectAsState()
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
    DockedSearchBar(
        query = state.query,
        onQueryChange = {
            state.updateQuery(it)
            onSearch(it)
        },
        onSearch = {
            onSearch(it)
        },
        active = nameSearchActive,
        onActiveChange = {},
        modifier = Modifier.then(modifier),
        shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
        placeholder = placeholder,
        trailingIcon = trailingIcon,
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
    XentlyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            val suggestions = List(Random.nextInt(0, 10)) {
                buildString {
                    append("Suggestion ")
                    append(it + 1)
                }
            }
            AutoCompleteTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberAutoCompleteTextFieldState(suggestions = suggestions),
                onSearch = {},
                onSuggestionSelected = {},
                onSearchSuggestionSelected = {},
                placeholder = {
                    Text(text = "Search...")
                },
                suggestionContent = {
                    Text(text = it)
                },
            )
        }
    }
}