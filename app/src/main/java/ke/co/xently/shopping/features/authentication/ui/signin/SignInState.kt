package ke.co.xently.shopping.features.authentication.ui.signin

sealed interface SignInState {
    object Idle : SignInState
    object Success : SignInState
    data class Failure(val error: Throwable) : SignInState
    object Loading : SignInState
}