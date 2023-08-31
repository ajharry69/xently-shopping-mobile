package ke.co.xently.shopping.features.recommendations.ui.response

import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse

sealed interface RecommendationResponseState {
    object Idle : RecommendationResponseState
    data class Success(
        val data: RecommendationResponse.ViewModel,
        val sortParameter: SortParameter,
    ) : RecommendationResponseState

    data class Failure(val error: Throwable) : RecommendationResponseState
    object Loading : RecommendationResponseState
}