package ke.co.xently.shopping.features.authentication.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.repositories.AuthenticationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthenticationRepository,
) : ViewModel() {
    private val stateChannel = Channel<SignInState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun signIn(request: SignInRequest) {
        viewModelScope.launch {
            stateChannel.send(SignInState.Loading)

            repository.signIn(request).also { result ->
                result.onSuccess {
                    stateChannel.send(SignInState.Success)
                }.onFailure {
                    Timber.tag(TAG).e(it, "signIn: %s", it.localizedMessage)
                    stateChannel.send(SignInState.Failure(it))
                }
            }
        }
    }

    companion object {
        private val TAG: String = SignInViewModel::class.java.simpleName
    }
}