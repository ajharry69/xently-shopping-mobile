package ke.co.xently.shopping.features.users

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class User(
    @PrimaryKey
    val uid: Int = 1,
    val id: Long = -1,
    val firstName: String? = null,
    val lastName: String? = null,
    val token: String? = null,
    val expiry: Long = DEFAULT_EXPIRY,
    val expiryUnit: String? = null,
    val dateAddedEpochSecond: Long = Instant.now().epochSecond,
) {
    companion object {
        const val DEFAULT_EXPIRY: Long = 24 * 60 * 60
    }
}
