package ke.co.xently.features.locationtracker

import androidx.compose.runtime.staticCompositionLocalOf

val LocalLocationPermissionsState = staticCompositionLocalOf<LocationPermissionsState> {
    LocationPermissionsState.Simulated
}