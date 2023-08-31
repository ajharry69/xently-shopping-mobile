package ke.co.xently.shopping.features.recommendations.ui

sealed interface State {
    object Idle : State
    object GettingCurrentLocation : State
    object Success : State
    data class Failure(val error: Throwable) : State
    object Loading : State
}