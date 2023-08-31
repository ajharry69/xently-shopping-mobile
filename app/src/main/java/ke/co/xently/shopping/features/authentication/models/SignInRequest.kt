package ke.co.xently.shopping.features.authentication.models

data class SignInRequest(
    val email: String,
    val password: String,
)