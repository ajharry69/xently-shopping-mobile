package ke.co.xently.recommendations.datasource

import ke.co.xently.recommendations.models.Recommendation

interface RecommendationDataSource<TRequest : Recommendation, TResponse : Recommendation> {
    suspend fun getRecommendations(request: TRequest): List<TResponse>
}