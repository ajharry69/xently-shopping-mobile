package ke.co.xently.shopping.features.recommendations.models

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID


sealed interface RecommendationResponse {
    val requestId: UUID
    val serviceCharge: Double

    @Keep
    data class ViewModel(
        override val requestId: UUID,
        override val serviceCharge: Double,
        val recommendations: List<Recommendation.Response>,
        val isPaid: Boolean = false,
    ) : RecommendationResponse

    @Keep
    data class ServerSide(
        override val requestId: UUID,
        override val serviceCharge: Double,
        val recommendations: List<Recommendation.Response>,
    ) : RecommendationResponse

    @Keep
    @Entity("recommendations")
    data class LocalCache(
        @PrimaryKey
        override val requestId: UUID,
        override val serviceCharge: Double,
        val recommendationsJson: String,
        @Embedded
        val decryptionCredentials: DecryptionCredentials? = null,
        val dateAddedEpochSeconds: Long = Instant.now().epochSecond,
        val dateUpdatedEpochSeconds: Long = Instant.now().epochSecond,
    ) : RecommendationResponse
}