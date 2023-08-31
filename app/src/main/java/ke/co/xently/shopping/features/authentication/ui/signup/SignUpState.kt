package ke.co.xently.shopping.features.authentication.ui.signup

sealed interface SignUpState {
    object Idle : SignUpState
    object Success : SignUpState
    data class Failure(val error: Throwable) : SignUpState
    object Loading : SignUpState
}