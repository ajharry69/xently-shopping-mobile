package ke.co.xently.shopping.features.authentication.ui.requestpasswordreset

sealed interface RequestPasswordResetState {
    object Idle : RequestPasswordResetState
    object Success : RequestPasswordResetState
    data class Failure(val error: Throwable) : RequestPasswordResetState
    object Loading : RequestPasswordResetState
}