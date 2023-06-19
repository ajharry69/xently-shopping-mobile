package ke.co.xently.recommendations.ui

import ke.co.xently.recommendations.models.Recommendation

sealed interface State {
    object Idle : State
    data class Success(private val data: List<Recommendation.Response>) : State
    data class Failure(private val error: Throwable) : State
    object Loading : State
}