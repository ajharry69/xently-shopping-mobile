package ke.co.xently.shopping.features.authentication.models

import androidx.annotation.Keep
import ke.co.xently.shopping.features.users.User

@Keep
data class ResetPasswordResponse(
    val token: String,
    val expiry: Long = User.DEFAULT_EXPIRY,
    val expiryUnit: String? = null,
) {
    val user: User
        get() = User(
            token = token,
            expiry = expiry,
            expiryUnit = expiryUnit,
        )
}