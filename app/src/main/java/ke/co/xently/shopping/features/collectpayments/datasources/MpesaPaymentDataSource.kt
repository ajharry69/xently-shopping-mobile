package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest


abstract class MpesaPaymentDataSource {
    open suspend fun pay(request: MpesaPaymentRequest) {
        TODO("Not yet implemented")
    }

    open suspend fun getLatestUnprocessedRecommendationRequestId(): Long {
        TODO("Not yet implemented")
    }
}
