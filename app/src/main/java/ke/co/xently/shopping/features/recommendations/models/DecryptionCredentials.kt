package ke.co.xently.shopping.features.recommendations.models

import androidx.annotation.Keep

@Keep
data class DecryptionCredentials(
    val secretKeyPassword: String,
    val base64EncodedIVParameterSpec: String
)