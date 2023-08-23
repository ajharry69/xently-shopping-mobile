package ke.co.xently.shopping.features.authentication.models

data class ResetPasswordRequest(val temporaryPassword: String, val newPassword: String)