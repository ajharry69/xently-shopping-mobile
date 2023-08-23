package ke.co.xently.shopping.features.authentication.models

data class RequestPasswordResetResponse(
    val token: String,
    val expiry: Long = -1,
    val expiryUnit: String? = null,
)