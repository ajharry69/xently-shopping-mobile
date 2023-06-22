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
import androidx.compose.runtime.derivedStateOf
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ke.co.xently.R
import ke.co.xently.products.models.Store
import ke.co.xently.products.models.toLocation
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
    var location by remember {
        mutableStateOf(store.location)
    }
    val isLocationUsable by remember(location) {
        derivedStateOf {
            location.isUsable()
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_store_page_title,
        subHeading = R.string.xently_add_store_page_sub_heading,
        showBackButton = false,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = isLocationUsable,
                onClick = {
                    store.toLocalViewModel().copy(
                        name = nameQuery,
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(if (isLocationUsable) R.string.xently_button_label_continue else R.string.xently_button_label_select_store_location))
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
            val markerState = rememberMarkerState()
            val cameraPositionState: CameraPositionState = rememberCameraPositionState()
            var wasDraftSaveTriggeredFromAPointOfInterest by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(location) {
                if (wasDraftSaveTriggeredFromAPointOfInterest) {
                    // Reset until another POI click happens
                    wasDraftSaveTriggeredFromAPointOfInterest = false
                } else {
                    // POI clicks can trigger manual draft saves
                    store.toLocalViewModel().copy(location = location)
                        .let(saveDraft)
                }

                if (!isLocationUsable) {
                    return@LaunchedEffect
                }
                val locationLatLng = location.toLatLng()
                markerState.position = locationLatLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(locationLatLng, 18f)
            }

            var wasMarkerDragStarted by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(markerState.dragState) {
                when (markerState.dragState) {
                    DragState.END -> {
                        if (wasMarkerDragStarted) {
                            // DragState.END is called by default, but we only want to
                            // update the location state if the drag was actually
                            // triggered. A triggered drag must first have the state
                            // as START.
                            location = markerState.position.toLocation()
                            wasMarkerDragStarted = false
                        }
                    }

                    DragState.START -> {
                        wasMarkerDragStarted = true
                    }

                    DragState.DRAG -> {
                    }
                }
            }

            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                contentDescription = stringResource(R.string.xently_content_description_store_map),
                onMapLoaded = {
                    showMap = true
                },
                onMapClick = {
                    location = it.toLocation()
                },
                onMyLocationClick = {
                    location = it.toLocation()
                },
                onPOIClick = { poi: PointOfInterest ->
                    location = poi.latLng.toLocation().also {
                        // TODO: Consider if store name should be overridden if already provided
                        //  like we currently do
                        store.toLocalViewModel().copy(name = poi.name, location = it)
                            .let(saveDraft)
                        wasDraftSaveTriggeredFromAPointOfInterest = true
                    }
                },
            ) {
                Marker(
                    state = markerState,
                    draggable = true,
                    visible = isLocationUsable,
                    onClick = {
                        location = it.position.toLocation()
                        true
                    },
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !showMap,
                modifier = Modifier.matchParentSize(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
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