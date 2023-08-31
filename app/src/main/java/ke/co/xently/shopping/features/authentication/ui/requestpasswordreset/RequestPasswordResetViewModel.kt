package ke.co.xently.shopping.features.authentication.ui.requestpasswordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.repositories.AuthenticationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RequestPasswordResetViewModel @Inject constructor(
    private val repository: AuthenticationRepository,
) : ViewModel() {
    private val stateChannel = Channel<RequestPasswordResetState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun requestPasswordReset(request: RequestPasswordResetRequest) {
        viewModelScope.launch {
            stateChannel.send(RequestPasswordResetState.Loading)

            repository.requestPasswordReset(request).also { result ->
                result.onSuccess {
                    stateChannel.send(RequestPasswordResetState.Success)
                }.onFailure {
                    Timber.tag(TAG).e(it, "requestPasswordReset: %s", it.localizedMessage)
                    stateChannel.send(RequestPasswordResetState.Failure(it))
                }
            }
        }
    }

    companion object {
        private val TAG: String = RequestPasswordResetViewModel::class.java.simpleName
    }
}