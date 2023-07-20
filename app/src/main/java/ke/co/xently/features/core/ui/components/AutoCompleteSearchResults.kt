package ke.co.xently.features.core.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import ke.co.xently.features.core.ui.utils.CallOnLifecycleEvent
import ke.co.xently.remotedatasource.services.AutoCompleteService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun <Q> AutoCompleteSearchResults(
    service: AutoCompleteService<Q>,
    suggestions: (AutoCompleteService.ResultState) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current

    CallOnLifecycleEvent {
        coroutineScope.launch {
            if (it == Lifecycle.Event.ON_PAUSE) {
                service.closeSession()
            } else if (it == Lifecycle.Event.ON_RESUME) {
                // Ensure previous sessions are closed before initialising another
                // this is a defensive move aimed at preventing the possibility of
                // running into unexpected errors.
                service.closeSession()

                val initState = service.initSession()
                while (initState !is AutoCompleteService.InitState.Success && initState !is AutoCompleteService.InitState.Failure) {
                    delay(100.milliseconds)
                    Log.i(
                        "AutoCompleteSearchResults",
                        "Response: ${initState}. Retrying service initialization. Please wait...",
                    )
                }
                service.getSearchResults()
                    .flowWithLifecycle(
                        lifecycle = lifecycle.lifecycle,
                        minActiveState = Lifecycle.State.RESUMED,
                    )
                    .collect(suggestions)
            }
        }
    }
}