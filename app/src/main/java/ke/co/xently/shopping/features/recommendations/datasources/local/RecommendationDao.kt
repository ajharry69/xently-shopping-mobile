package ke.co.xently.shopping.features.recommendations.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface RecommendationDao {
    @Insert
    suspend fun save(response: RecommendationResponse.LocalCache)

    @Query("SELECT * FROM recommendations ORDER BY requestId DESC")
    fun getLatestRecommendationResponse(): Flow<RecommendationResponse.LocalCache?>

    @Query("SELECT requestId FROM recommendations")
    suspend fun getLatestUnprocessedRecommendationRequestId(): Long

    @Query(
        """
            UPDATE recommendations SET secretKeyPassword = :secretKeyPassword,
                base64EncodedIVParameterSpec = :base64EncodedIVParameterSpec,
                dateUpdatedEpochSeconds = CURRENT_TIMESTAMP
            WHERE requestId = :requestId
        """,
    )
    suspend fun saveDecryptionCredentials(
        requestId: Long,
        secretKeyPassword: String,
        base64EncodedIVParameterSpec: String,
    )
}
