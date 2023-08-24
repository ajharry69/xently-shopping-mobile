package ke.co.xently.shopping.features.authentication.models

import ke.co.xently.shopping.features.users.User

data class ResetPasswordResponse(
    val token: String,
    val expiry: Long = -1,
    val expiryUnit: String? = null,
) {
    val user: User
        get() = User(
            token = token,
            expiry = expiry,
            expiryUnit = expiryUnit,
        )
}