package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest


class LocalMpesaPaymentDataSource(private val database: Database) : MpesaPaymentDataSource() {
    override suspend fun getAuthorizationToken(): String? {
        return database.userDao.getAuthorizationToken()
    }

    override suspend fun getLatestUnprocessedRecommendationRequestId(): Long {
        return database.recommendationDao.getLatestUnprocessedRecommendationRequestId()
    }
}