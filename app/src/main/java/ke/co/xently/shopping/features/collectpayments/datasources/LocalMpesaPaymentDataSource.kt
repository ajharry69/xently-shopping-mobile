package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.datasource.local.Database


class LocalMpesaPaymentDataSource(private val database: Database) : MpesaPaymentDataSource() {
    override suspend fun getLatestUnprocessedRecommendationRequestId(): Long {
        return database.recommendationDao.getLatestUnprocessedRecommendationRequestId()
    }
}