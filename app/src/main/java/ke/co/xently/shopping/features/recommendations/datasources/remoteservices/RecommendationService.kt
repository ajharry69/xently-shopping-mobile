package ke.co.xently.shopping.features.recommendations.datasources.remoteservices

import ke.co.xently.shopping.features.recommendations.models.DecryptionCredentials
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface RecommendationService {
    @POST("recommendations")
    suspend fun get(@Body request: Recommendation.Request): Response<RecommendationResponse.ServerSide>

    @GET("recommendations/{requestId}/decryption-credentials")
    suspend fun getDecryptionCredentials(@Path("requestId") requestId: UUID): Response<DecryptionCredentials>
}