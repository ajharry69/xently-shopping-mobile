package ke.co.xently.features.recommendations.ui

import ke.co.xently.features.recommendations.models.Recommendation

sealed interface State {
    object Idle : State
    object GettingCurrentLocation : State
    data class Success(val data: List<Recommendation.Response>) : State
    data class Failure(val error: Throwable) : State
    object Loading : State
}