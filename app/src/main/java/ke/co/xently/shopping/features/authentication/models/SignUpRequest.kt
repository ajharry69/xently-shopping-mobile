package ke.co.xently.shopping.features.authentication.models

import androidx.annotation.Keep

@Keep
data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)