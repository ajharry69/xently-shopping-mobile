package ke.co.xently.locationtracker

import android.location.Location

sealed interface LocationTrackingState {
    object Idle : LocationTrackingState
    data class Success(val data: Location) : LocationTrackingState
    data class Failure(val throwable: Throwable) : LocationTrackingState
}