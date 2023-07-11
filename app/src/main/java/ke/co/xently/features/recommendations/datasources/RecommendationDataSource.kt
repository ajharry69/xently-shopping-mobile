package ke.co.xently.features.recommendations.datasources

import ke.co.xently.features.recommendations.models.Recommendation

interface RecommendationDataSource<TRequest : Recommendation, TResponse : Recommendation> {
    suspend fun getRecommendations(request: TRequest): List<TResponse>
}