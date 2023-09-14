package ke.co.xently.shopping.features.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.features.core.ui.utils.CallOnLifecycleEvent

@Composable
fun <Q> AutoCompleteSearchResults(
    service: AutoCompleteService<Q>,
    suggestions: (AutoCompleteService.ResultState) -> Unit,
) {
    val initState by produceState<AutoCompleteService.InitState>(
        AutoCompleteService.InitState.Idle,
        service,
    ) {
        value = service.initSession()
    }

    val lifecycle = LocalLifecycleOwner.current
    val currentSuggestions by rememberUpdatedState(suggestions)
    if (initState is AutoCompleteService.InitState.Success) {
        LaunchedEffect(service, lifecycle) {
            service.getSearchResults().flowWithLifecycle(
                lifecycle = lifecycle.lifecycle,
                minActiveState = Lifecycle.State.RESUMED,
            ).collect(currentSuggestions)
        }
    }

    var lifecycleEvent: Lifecycle.Event? by rememberSaveable {
        mutableStateOf(null)
    }

    if (lifecycleEvent == Lifecycle.Event.ON_STOP) {
        LaunchedEffect(Unit) {
            service.closeSession()
        }
    }

    CallOnLifecycleEvent {
        lifecycleEvent = it
    }

    /*val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(closeSessionKey()) {
        service.closeSession()
    }

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
                    delay(250.milliseconds)
                    Timber.tag("AutoCompleteSearchResults").i(
                        "Response: %s. Retrying service initialization. Please wait...",
                        initState,
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
    }*/
}