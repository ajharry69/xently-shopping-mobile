package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.features.collectpayments.datasources.remoteservices.MpesaPaymentService
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest

class RemoteMpesaPaymentDataSource(
    private val service: MpesaPaymentService,
) : MpesaPaymentDataSource() {
    override suspend fun pay(request: MpesaPaymentRequest) {
        service.pay(request)
    }
}