package ke.co.xently.shopping.features.authentication.ui.resetpassword

sealed interface ResetPasswordState {
    object Idle : ResetPasswordState
    object Success : ResetPasswordState
    data class Failure(val error: Throwable) : ResetPasswordState
    object Loading : ResetPasswordState
}