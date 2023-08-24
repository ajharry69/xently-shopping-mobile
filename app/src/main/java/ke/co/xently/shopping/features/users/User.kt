package ke.co.xently.shopping.features.users

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = -1,
    val firstName: String? = null,
    val lastName: String? = null,
    val token: String? = null,
    val expiry: Long = -1,
    val expiryUnit: String? = null,
    val dateAddedEpochSecond: Long = Instant.now().epochSecond,
)
