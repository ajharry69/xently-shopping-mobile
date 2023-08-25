package ke.co.xently.shopping.features.collectpayments.ui

sealed interface MpesaPaymentRequestState {
    object Idle : MpesaPaymentRequestState
    object Success : MpesaPaymentRequestState
    data class Failure(val error: Throwable) : MpesaPaymentRequestState
    object Loading : MpesaPaymentRequestState
}