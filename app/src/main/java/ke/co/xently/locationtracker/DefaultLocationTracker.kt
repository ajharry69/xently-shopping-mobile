package ke.co.xently.locationtracker

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import ke.co.xently.locationtracker.exceptions.GPSNotEnabledException
import ke.co.xently.locationtracker.exceptions.LocationTrackerException
import ke.co.xently.locationtracker.exceptions.MissingLocationTrackingPermissionsException
import ke.co.xently.locationtracker.exceptions.NullLocationResultsException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLocationTracker @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: Application,
) : LocationTracker {
    override fun isGPSEnabled(): Boolean {
        val locationManager = application.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        return locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentLocation(): Result<Location> {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGPSEnabled()) {
            if (!(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
                return Result.failure(MissingLocationTrackingPermissionsException())
            }
            return Result.failure(GPSNotEnabledException())
        }

        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                addOnSuccessListener {
                    val res = if (it == null) {
                        Result.failure(NullLocationResultsException())
                    } else {
                        Result.success(it)
                    }
                    cont.resume(res) {}
                }
                addOnFailureListener {
                    cont.resume(Result.failure(LocationTrackerException(it))) {}
                }
                addOnCanceledListener {
                    cont.cancel() // Cancel the coroutine
                }
            }
        }
    }
}