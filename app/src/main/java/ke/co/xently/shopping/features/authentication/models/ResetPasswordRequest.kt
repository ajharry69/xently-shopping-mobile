package ke.co.xently.shopping.features.authentication.models

import androidx.annotation.Keep

@Keep
data class ResetPasswordRequest(val temporaryPassword: String, val newPassword: String)