package ke.co.xently.features.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import ke.co.xently.remotedatasource.services.AutoCompleteService

@Composable
fun <Q, R> AutoCompleteSearchResults(
    service: AutoCompleteService<Q, R>,
    shouldShowSuggestions: () -> Boolean,
    suggestions: (List<R>) -> Unit,
) {
    val showSuggestions by remember {
        derivedStateOf(shouldShowSuggestions)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (showSuggestions) {
        LaunchedEffect(true) {
            val result = service.initSession()

            result.onSuccess {
                service.getSearchResults()
                    .flowWithLifecycle(
                        lifecycleOwner.lifecycle,
                        minActiveState = Lifecycle.State.RESUMED,
                    )
                    .collect(suggestions)
            }
        }
    } else {
        LaunchedEffect(true) {
            service.closeSession()
        }
    }
}