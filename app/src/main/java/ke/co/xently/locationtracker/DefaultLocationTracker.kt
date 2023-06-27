package ke.co.xently.locationtracker

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
    companion object {
        private val TAG = DefaultLocationTracker::class.java.simpleName
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

        if (!isGPSEnabled(application)) {
            if (!(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
                return Result.failure(MissingLocationTrackingPermissionsException())
            }
            return Result.failure(GPSNotEnabledException())
        }

        Log.i(TAG, "Getting current device location...")
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