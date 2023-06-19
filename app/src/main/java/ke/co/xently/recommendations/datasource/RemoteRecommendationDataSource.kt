package ke.co.xently.recommendations.datasource

import ke.co.xently.recommendations.models.Recommendation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRecommendationDataSource @Inject constructor() :
    RecommendationDataSource<Recommendation.Request, Recommendation.Response> {
    override suspend fun getRecommendations(request: Recommendation.Request): List<Recommendation.Response> {
        return emptyList()
    }
}