package ke.co.xently.shopping.features.recommendations.datasources

import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.recommendations.models.DecryptionCredentials
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import ke.co.xently.shopping.features.recommendations.models.toLocalCache
import kotlinx.coroutines.flow.Flow

class LocalRecommendationDataSource(private val database: Database) : RecommendationDataSource() {
    override suspend fun saveDecryptionCredentials(
        requestId: Long,
        credentials: DecryptionCredentials
    ) {
        database.recommendationDao.saveDecryptionCredentials(
            requestId,
            credentials.secretKeyPassword,
            credentials.base64EncodedIVParameterSpec,
        )
    }

    override fun getLatestRecommendations(): Flow<RecommendationResponse?> {
        return database.recommendationDao.getLatestRecommendationResponse()
    }

    override suspend fun saveRecommendationResponse(response: RecommendationResponse) {
        database.recommendationDao.save(response.toLocalCache())
    }

    override suspend fun getLatestUnprocessedRecommendationRequestId(): Long {
        return database.recommendationDao.getLatestUnprocessedRecommendationRequestId()
    }
}
