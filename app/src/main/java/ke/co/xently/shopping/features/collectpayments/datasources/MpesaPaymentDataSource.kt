package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import java.util.UUID


abstract class MpesaPaymentDataSource {
    open suspend fun pay(request: MpesaPaymentRequest) {
        TODO("Not yet implemented")
    }

    open suspend fun getLatestUnprocessedRecommendationRequestId(): UUID {
        TODO("Not yet implemented")
    }
}
