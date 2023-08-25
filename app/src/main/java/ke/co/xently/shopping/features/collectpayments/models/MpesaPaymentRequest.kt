package ke.co.xently.shopping.features.collectpayments.models

data class MpesaPaymentRequest(val phoneNumber: Long, val recommendationRequestId: Long = -1)
