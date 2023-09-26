package ke.co.xently.shopping.features.collectpayments.models

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class MpesaPaymentRequest(
    val phoneNumber: Long,
    val recommendationRequestId: UUID = UUID.randomUUID()
)
