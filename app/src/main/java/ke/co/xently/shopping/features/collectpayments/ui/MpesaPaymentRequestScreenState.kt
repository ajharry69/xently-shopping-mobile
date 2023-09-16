package ke.co.xently.shopping.features.collectpayments.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class MpesaPaymentRequestScreenState(message: Int) : UIState(message) {
    object OK : MpesaPaymentRequestScreenState(android.R.string.ok)
    sealed class PhoneNumber(message: Int) : MpesaPaymentRequestScreenState(message) {
        object Blank : PhoneNumber(R.string.xently_error_missing_phone_number)
    }
}