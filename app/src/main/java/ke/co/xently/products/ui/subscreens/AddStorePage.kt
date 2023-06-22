package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import ke.co.xently.R
import ke.co.xently.products.models.Store
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStorePage(
    modifier: Modifier,
    store: Store,
    suggestionsState: StateFlow<List<Store>>,
    search: (Store) -> Unit,
    saveDraft: (Store) -> Unit,
    onSearchSuggestionSelected: () -> Unit,
    onContinueClick: (Store) -> Unit,
) {
    val suggestions by suggestionsState.collectAsState()
    var nameQuery by remember(store.name) {
        mutableStateOf(store.name)
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_store_page_title,
        subHeading = R.string.xently_add_store_page_sub_heading,
        showBackButton = false,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    store.toLocalViewModel().copy(
                        name = nameQuery,
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        var nameSearchActive by remember {
            mutableStateOf(false)
        }
        var wasSuggestionSelected by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(nameQuery, suggestions) {
            if (wasSuggestionSelected) {
                wasSuggestionSelected = false
            } else {
                nameSearchActive = nameQuery.isNotBlank() && suggestions.isNotEmpty()
            }
        }
        val doSearch: () -> Unit by rememberUpdatedState {
            Store.LocalViewModel.default.copy(name = nameQuery)
                .let(search)
        }
        DockedSearchBar(
            query = nameQuery,
            onQueryChange = {
                nameQuery = it
                doSearch()
            },
            onSearch = {
                doSearch()
            },
            active = nameSearchActive,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
            placeholder = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
        ) {
            for (suggestion in suggestions) {
                ListItem(
                    headlineContent = {
                        Text(text = suggestion.name)
                    },
                    modifier = Modifier.clickable {
                        saveDraft(suggestion)
                        wasSuggestionSelected = true
                        nameSearchActive = false
                        onSearchSuggestionSelected()
                    },
                )
            }
        }

        var showMap by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                onMapLoaded = {
                    showMap = true
                },
            )
            if (!showMap) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AddStorePagePreview() {
    XentlyTheme {
        AddStorePage(
            modifier = Modifier.fillMaxSize(),
            store = Store.LocalViewModel.default,
            suggestionsState = MutableStateFlow(emptyList()),
            search = {},
            saveDraft = {},
            onContinueClick = {},
            onSearchSuggestionSelected = {},
        )
    }
}