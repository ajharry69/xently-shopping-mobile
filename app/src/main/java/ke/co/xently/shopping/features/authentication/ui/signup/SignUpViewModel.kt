package ke.co.xently.shopping.features.authentication.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.repositories.AuthenticationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthenticationRepository,
) : ViewModel() {
    private val stateChannel = Channel<SignUpState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun signUp(request: SignUpRequest) {
        viewModelScope.launch {
            stateChannel.send(SignUpState.Loading)

            repository.signUp(request).also { result ->
                result.onSuccess {
                    stateChannel.send(SignUpState.Success)
                }.onFailure {
                    Timber.tag(TAG).e(it, "signUp: %s", it.localizedMessage)
                    stateChannel.send(SignUpState.Failure(it))
                }
            }
        }
    }

    companion object {
        private val TAG: String = SignUpViewModel::class.java.simpleName
    }
}