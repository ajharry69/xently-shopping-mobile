package ke.co.xently.shopping.features.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.features.core.ui.utils.CallOnLifecycleEvent

@Composable
fun <Q> AutoCompleteSearchResults(service: AutoCompleteService<Q>) {
    val initState by produceState<AutoCompleteService.InitState>(
        AutoCompleteService.InitState.Idle,
        service,
    ) {
        value = service.initSession()
    }

    if (initState is AutoCompleteService.InitState.Success) {
        LaunchedEffect(service) {
            service.initGetSearchResults()
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
}