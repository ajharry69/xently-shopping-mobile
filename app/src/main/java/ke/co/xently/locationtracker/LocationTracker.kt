package ke.co.xently.locationtracker

import android.location.Location
import ke.co.xently.locationtracker.exceptions.LocationTrackerException

/**
 * https://medium.com/@daniel.atitienei/get-current-user-location-in-jetpack-compose-using-clean-architecture-android-6683abca66c9
 */
interface LocationTracker {
    fun isGPSEnabled(): Boolean

    @Throws(LocationTrackerException::class)
    suspend fun getCurrentLocation(): Result<Location>
}
