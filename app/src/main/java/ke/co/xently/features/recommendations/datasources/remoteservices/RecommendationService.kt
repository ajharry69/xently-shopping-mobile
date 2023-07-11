package ke.co.xently.features.recommendations.datasources.remoteservices

import ke.co.xently.features.recommendations.models.Recommendation
import retrofit2.Response
import retrofit2.http.*

interface RecommendationService {
    @POST("recommendations")
    suspend fun get(@Body request: Recommendation.Request): Response<List<Recommendation.Response>>
}