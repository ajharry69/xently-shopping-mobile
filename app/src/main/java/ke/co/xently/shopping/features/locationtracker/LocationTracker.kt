package ke.co.xently.shopping.features.locationtracker

import android.location.Location

/**
 * Based on: https://medium.com/@daniel.atitienei/get-current-user-location-in-jetpack-compose-using-clean-architecture-android-6683abca66c9
 */
fun interface LocationTracker {
    suspend fun getCurrentLocation(): Result<Location>
}
