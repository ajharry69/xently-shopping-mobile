package ke.co.xently.shopping.features.recommendations.datasources

import ke.co.xently.shopping.features.recommendations.models.DecryptionCredentials
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import kotlinx.coroutines.flow.Flow
import java.util.UUID

abstract class RecommendationDataSource {
    open fun getLatestRecommendations(): Flow<RecommendationResponse?> {
        TODO("Not implemented")
    }

    open suspend fun getRecommendations(request: Recommendation.Request): RecommendationResponse {
        TODO("Not implemented")
    }

    open suspend fun getDecryptionCredentials(requestId: UUID): DecryptionCredentials {
        TODO("Not implemented")
    }

    open suspend fun saveRecommendationResponse(response: RecommendationResponse) {
        TODO("Not implemented")
    }

    open suspend fun saveDecryptionCredentials(
        requestId: UUID,
        credentials: DecryptionCredentials,
    ) {
        TODO("Not implemented")
    }

    open suspend fun getLatestUnprocessedRecommendationRequestId(): UUID {
        TODO("Not yet implemented")
    }
}