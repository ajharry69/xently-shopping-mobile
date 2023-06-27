package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ke.co.xently.R
import ke.co.xently.locationtracker.ForegroundLocationTracker
import ke.co.xently.locationtracker.LocationPermissionsState
import ke.co.xently.products.models.Store
import ke.co.xently.products.models.toLocation
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.products.ui.components.AutoCompleteTextField
import ke.co.xently.products.ui.components.rememberAutoCompleteTextFieldState
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddStorePage(
    modifier: Modifier,
    store: Store,
    snackbarHostState: SnackbarHostState,
    suggestionsState: StateFlow<List<Store>>,
    permissionsState: LocationPermissionsState,
    search: (Store) -> Unit,
    saveDraft: (Store) -> Unit,
    onSearchSuggestionSelected: () -> Unit,
    onContinueClick: (Store) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(
        query = store.name,
        suggestionsState = suggestionsState,
    )

    var location by remember {
        mutableStateOf(store.location)
    }
    val isLocationUsable by remember(location) {
        derivedStateOf {
            location.isUsable()
        }
    }

    ForegroundLocationTracker(
        permissionsState = permissionsState,
        snackbarHostState = snackbarHostState,
    ) {
        if (!isLocationUsable) {
            location = it.toLocation()
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_store_page_title,
        subheading = R.string.xently_add_store_page_sub_heading,
        showBackButton = false,
        scrollState = null,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = isLocationUsable,
                onClick = {
                    store.toLocalViewModel().copy(
                        name = nameAutoCompleteState.query,
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(if (isLocationUsable) R.string.xently_button_label_continue else R.string.xently_button_label_select_store_location))
            }
        },
    ) {
        AutoCompleteTextField(
            modifier = Modifier.fillMaxWidth(),
            state = nameAutoCompleteState,
            onSearch = { query ->
                Store.LocalViewModel.default.copy(name = query)
                    .let(search)
            },
            onSuggestionSelected = saveDraft,
            onSearchSuggestionSelected = onSearchSuggestionSelected,
            suggestionContent = { Text(text = it.name) },
            placeholder = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
        )

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
                    location = poi.latLng.toLocation().also { poiLocation ->
                        // TODO: Consider if store name should be overridden if already provided
                        //  like we currently do
                        val name = poi.name.split("\n").joinToString {
                            it.trim()
                        }
                        store.toLocalViewModel().copy(name = name, location = poiLocation)
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
private fun AddStorePagePreview() {
    XentlyTheme {
        AddStorePage(
            modifier = Modifier.fillMaxSize(),
            store = Store.LocalViewModel.default,
            snackbarHostState = SnackbarHostState(),
            suggestionsState = MutableStateFlow(emptyList()),
            permissionsState = LocationPermissionsState.Simulated,
            search = {},
            saveDraft = {},
            onSearchSuggestionSelected = {},
        ) {}
    }
}