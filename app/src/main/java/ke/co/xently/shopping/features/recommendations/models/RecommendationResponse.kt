package ke.co.xently.shopping.features.recommendations.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant


sealed interface RecommendationResponse {
    val requestId: Long
    val serviceCharge: Double

    data class ViewModel(
        override val requestId: Long,
        override val serviceCharge: Double,
        val recommendations: List<Recommendation.Response>,
        val isPaid: Boolean = false,
    ) : RecommendationResponse

    data class ServerSide(
        override val requestId: Long,
        override val serviceCharge: Double,
        val recommendations: List<Recommendation.Response>,
    ) : RecommendationResponse

    @Entity("recommendations")
    data class LocalCache(
        @PrimaryKey
        override val requestId: Long,
        override val serviceCharge: Double,
        val recommendationsJson: String,
        @Embedded
        val decryptionCredentials: DecryptionCredentials? = null,
        val dateAddedEpochSeconds: Long = Instant.now().epochSecond,
        val dateUpdatedEpochSeconds: Long = Instant.now().epochSecond,
    ) : RecommendationResponse
}