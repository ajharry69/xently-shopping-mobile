package ke.co.xently.shopping.features.recommendations.models

data class DecryptionCredentials(
    val secretKeyPassword: String,
    val base64EncodedIVParameterSpec: String
)