package ke.co.xently.shopping.features.store.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.models.Location
import ke.co.xently.shopping.features.core.models.toLocation
import ke.co.xently.shopping.features.core.ui.AutoCompleteTextField
import ke.co.xently.shopping.features.core.ui.MultiStepScreen
import ke.co.xently.shopping.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.locationtracker.ForegroundLocationTracker
import ke.co.xently.shopping.features.locationtracker.LocalLocationPermissionsState
import ke.co.xently.shopping.features.store.models.Store

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddStorePage(
    modifier: Modifier,
    store: Store,
    saveDraft: (Store) -> Unit,
    onContinueClick: (Store) -> Unit,
) {
    var location by remember(store.location) {
        mutableStateOf(store.location)
    }
    val isLocationUsable by remember(location) {
        derivedStateOf {
            location.isUsable()
        }
    }

    var currentLocation: Location? by remember {
        mutableStateOf(null)
    }

    ForegroundLocationTracker(snackbarHostState = LocalSnackbarHostState.current) { myLocation ->
        currentLocation = myLocation.toLocation()
        if (!isLocationUsable) {
            location = currentLocation!!
        }
    }

    val nameAutoCompleteState = rememberAutoCompleteTextFieldState(store.name)

    MultiStepScreen(
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
        val focusManager = LocalFocusManager.current
        var invalidateActiveSearch by remember {
            mutableStateOf(false)
        }
        AutoCompleteTextField<Store, Store>(
            modifier = Modifier.fillMaxWidth(),
            invalidateActiveSearch = invalidateActiveSearch,
            service = LocalStoreAutoCompleteService.current,
            state = nameAutoCompleteState,
            onSearch = { query ->
                Store.LocalViewModel.default.run {
                    copy(name = query, location = currentLocation ?: this.location)
                }
            },
            onSuggestionSelected = saveDraft,
            suggestionContent = { Text(text = it.toLocalViewModel().toString()) },
            label = {
                Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
            },
            keyboardActions = KeyboardActions {
                invalidateActiveSearch = !invalidateActiveSearch
                focusManager.clearFocus()
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

            val locationPermissions = LocalLocationPermissionsState.current()

            val (mapUiSettings, mapProperties) = remember(locationPermissions.allPermissionsGranted) {
                MapUiSettings(myLocationButtonEnabled = locationPermissions.allPermissionsGranted) to
                        MapProperties(isMyLocationEnabled = locationPermissions.allPermissionsGranted)
            }

            GoogleMap(
                modifier = Modifier.matchParentSize(),
                uiSettings = mapUiSettings,
                properties = mapProperties,
                cameraPositionState = cameraPositionState,
                contentDescription = stringResource(R.string.xently_content_description_store_map),
                onMapLoaded = {
                    showMap = true
                },
                onMapClick = {
                    location = it.toLocation()
                },
                onMyLocationClick = { myLocation ->
                    location = myLocation.toLocation()
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
            saveDraft = {},
        ) {}
    }
}