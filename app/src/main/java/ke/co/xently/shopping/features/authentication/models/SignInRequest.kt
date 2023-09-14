package ke.co.xently.shopping.features.authentication.models

import androidx.annotation.Keep

@Keep
data class SignInRequest(
    val email: String,
    val password: String,
)