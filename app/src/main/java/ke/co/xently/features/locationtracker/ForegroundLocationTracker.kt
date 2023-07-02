package ke.co.xently.features.locationtracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import ke.co.xently.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ForegroundLocationTracker"

private object SimulatedDisposableEffectResult : DisposableEffectResult {
    override fun dispose() {

    }
}

@OptIn(ExperimentalPermissionsApi::class)
private object SimulatedMultiplePermissionsState : MultiplePermissionsState {
    override val allPermissionsGranted: Boolean
        get() = false
    override val permissions: List<PermissionState>
        get() = emptyList()
    override val revokedPermissions: List<PermissionState>
        get() = emptyList()
    override val shouldShowRationale: Boolean
        get() = false

    override fun launchMultiplePermissionRequest() {

    }
}

@OptIn(ExperimentalPermissionsApi::class)
sealed interface LocationPermissionsState {
    @Composable
    operator fun invoke(): MultiplePermissionsState

    object CoarseAndFine : LocationPermissionsState {
        @Composable
        override fun invoke(): MultiplePermissionsState {
            return rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        }
    }

    /**
     * This could be used in a compose preview
     */
    object Simulated : LocationPermissionsState {
        @Composable
        override fun invoke(): MultiplePermissionsState {
            return remember {
                SimulatedMultiplePermissionsState
            }
        }
    }
}

private suspend fun requestToEnableGPS(context: Context, snackbarHostState: SnackbarHostState) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    val canNavigateToGPSSettings =
        intent.resolveActivity(context.packageManager) != null

    val result = snackbarHostState.showSnackbar(
        message = context.getString(R.string.xently_gps_tracking_disabled),
        actionLabel = if (!canNavigateToGPSSettings) {
            null
        } else {
            context.getString(R.string.xently_snackbar_action_label_enable)
                .toUpperCase(Locale.current)
        },
        withDismissAction = true,
        duration = SnackbarDuration.Indefinite,
    )

    when (result) {
        SnackbarResult.Dismissed -> {

        }

        SnackbarResult.ActionPerformed -> {
            if (canNavigateToGPSSettings) {
                context.startActivity(intent)
            }
        }
    }
}

fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun ForegroundLocationTracker(
    /**
     * Will be used to show a prompt for enabling GPS if it is disabled
     * or requesting location permissions if non has been granted.
     */
    snackbarHostState: SnackbarHostState,
    permissionsState: LocationPermissionsState = LocationPermissionsState.CoarseAndFine,
    onLocationUpdates: (Location) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissions = permissionsState()

    var isGPSEnabled by remember {
        mutableStateOf(isGPSEnabled(context))
    }

    LaunchedEffect(true) {
        while (true) {
            isGPSEnabled = isGPSEnabled(context)
            delay(2.seconds)
        }
    }

    DisposableEffect(
        isGPSEnabled,
        permissions.shouldShowRationale,
        permissions.allPermissionsGranted,
    ) {
        if (!permissions.allPermissionsGranted || permissions.shouldShowRationale) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    context.getString(R.string.xently_missing_required_location_permissions),
                    context.getString(R.string.xently_snackbar_action_label_grant)
                        .toUpperCase(Locale.current),
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite,
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {
                        permissions.launchMultiplePermissionRequest()
                    }
                }
            }
            return@DisposableEffect SimulatedDisposableEffectResult
        }

        if (!isGPSEnabled) {
            scope.launch {
                requestToEnableGPS(
                    context = context,
                    snackbarHostState = snackbarHostState,
                )
            }
            return@DisposableEffect SimulatedDisposableEffectResult
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L,
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                if (p0.isLocationAvailable) {
                    return super.onLocationAvailability(p0)
                }

                scope.launch {
                    requestToEnableGPS(
                        context = context,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.also(onLocationUpdates)?.also {
                    Log.i(TAG, "Current location: $it")
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )

        onDispose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}