package ke.co.xently.shopping.features.authentication.models

import androidx.annotation.Keep

@Keep
data class RequestPasswordResetRequest(val email: String)