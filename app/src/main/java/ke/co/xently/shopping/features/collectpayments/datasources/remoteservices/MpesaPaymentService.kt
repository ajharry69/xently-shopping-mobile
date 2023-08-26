package ke.co.xently.shopping.features.collectpayments.datasources.remoteservices

import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MpesaPaymentService {
    @POST("saf-mobile-money-payment/request-stk-push")
    suspend fun pay(@Body request: MpesaPaymentRequest): Response<Any>
}
