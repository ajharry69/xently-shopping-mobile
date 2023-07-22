package ke.co.xently.shopping.features.recommendations.datasources.remoteservices

import ke.co.xently.shopping.features.recommendations.models.Recommendation
import retrofit2.Response
import retrofit2.http.*

interface RecommendationService {
    @POST("recommendations")
    suspend fun get(@Body request: Recommendation.Request): Response<List<Recommendation.Response>>
}