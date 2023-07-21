package ke.co.xently.features.locationtracker

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.products.ui.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val LocalLocationPermissionsState = staticCompositionLocalOf<LocationPermissionsState> {
    LocationPermissionsState.Simulated
}

val LocalFlowOfSaveProductState = compositionLocalOf<Flow<State>> {
    flowOf(State.Idle)
}