package ke.co.xently.shopping.features.collectpayments.models

import androidx.annotation.Keep

@Keep
data class MpesaPaymentRequest(val phoneNumber: Long, val recommendationRequestId: Long = -1)
