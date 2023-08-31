package ke.co.xently.shopping.features.authentication.models

data class SignUpRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val password: String,
)