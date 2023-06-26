package ke.co.xently.locationtracker

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
object ComposePreviewMultiplePermissionsState : MultiplePermissionsState {
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