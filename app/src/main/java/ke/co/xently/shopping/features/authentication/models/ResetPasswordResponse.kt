package ke.co.xently.shopping.features.authentication.models

data class ResetPasswordResponse(
    val token: String,
    val expiry: Long = -1,
    val expiryUnit: String? = null,
)