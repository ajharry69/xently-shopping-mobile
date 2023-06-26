package ke.co.xently.recommendations.datasource.remoteservices

import ke.co.xently.recommendations.models.Recommendation
import retrofit2.Response
import retrofit2.http.*

interface RecommendationService {
    @POST("recommendations")
    suspend fun get(@Body request: Recommendation.Request): Response<List<Recommendation.Response>>
}