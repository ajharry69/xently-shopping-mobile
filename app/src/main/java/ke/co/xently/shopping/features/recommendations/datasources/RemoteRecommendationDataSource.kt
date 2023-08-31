package ke.co.xently.shopping.features.recommendations.datasources

import ke.co.xently.shopping.features.recommendations.datasources.remoteservices.RecommendationService
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.datasource.remote.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRecommendationDataSource @Inject constructor(private val service: RecommendationService) :
    RecommendationDataSource<Recommendation.Request, Recommendation.Response> {
    override suspend fun getRecommendations(request: Recommendation.Request): List<Recommendation.Response> {
        return SendHttpRequest {
            service.get(request)
        }.getOrThrow()
    }
}