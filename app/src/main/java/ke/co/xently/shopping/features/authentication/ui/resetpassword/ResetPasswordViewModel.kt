package ke.co.xently.shopping.features.authentication.ui.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.repositories.AuthenticationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthenticationRepository,
) : ViewModel() {
    private val stateChannel = Channel<ResetPasswordState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            stateChannel.send(ResetPasswordState.Loading)

            repository.resetPassword(request).also { result ->
                result.onSuccess {
                    stateChannel.send(ResetPasswordState.Success)
                }.onFailure {
                    Timber.tag(TAG).e(it, "resetPassword: %s", it.localizedMessage)
                    stateChannel.send(ResetPasswordState.Failure(it))
                }
            }
        }
    }

    companion object {
        private val TAG: String = ResetPasswordViewModel::class.java.simpleName
    }
}