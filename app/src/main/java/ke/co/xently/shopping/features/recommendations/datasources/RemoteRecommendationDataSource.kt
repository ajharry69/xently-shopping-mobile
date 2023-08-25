package ke.co.xently.shopping.features.recommendations.datasources

import ke.co.xently.shopping.features.recommendations.datasources.remoteservices.RecommendationService
import ke.co.xently.shopping.features.recommendations.models.DecryptionCredentials
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import ke.co.xently.shopping.remotedatasource.SendHttpRequest

class RemoteRecommendationDataSource(private val service: RecommendationService) :
    RecommendationDataSource() {
    override suspend fun getRecommendations(request: Recommendation.Request): RecommendationResponse {
        return SendHttpRequest {
            service.get(request)
        }.getOrThrow()
    }

    override suspend fun getDecryptionCredentials(requestId: Long): DecryptionCredentials {
        return SendHttpRequest {
            service.getDecryptionCredentials(requestId)
        }.getOrThrow()
    }
}