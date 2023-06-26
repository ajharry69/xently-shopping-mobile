package ke.co.xently.locationtracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationTrackerViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
) : ViewModel() {
    private val isGPSEnabledMutable = MutableStateFlow(false)

    val isGPSEnabled = isGPSEnabledMutable.asStateFlow()

    init {
        checkGPSEnabled()
    }

    private val locationTrackingStateMutable =
        MutableStateFlow<LocationTrackingState>(LocationTrackingState.Idle)

    val locationTrackingState = locationTrackingStateMutable.asStateFlow()

    fun getCurrentLocation() {
        viewModelScope.launch {
            locationTracker.getCurrentLocation().onSuccess {
                locationTrackingStateMutable.value = LocationTrackingState.Success(it)
            }.onFailure {
                locationTrackingStateMutable.value = LocationTrackingState.Failure(it)
            }
        }
    }

    private fun checkGPSEnabled() {
        viewModelScope.launch {
            while (true) {
                isGPSEnabledMutable.value = locationTracker.isGPSEnabled()
                delay(1.seconds)
            }
        }
    }
}