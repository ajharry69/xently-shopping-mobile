package ke.co.xently.shopping.features.collectpayments.datasources

import ke.co.xently.shopping.datasource.local.Database
import java.util.UUID


class LocalMpesaPaymentDataSource(private val database: Database) : MpesaPaymentDataSource() {
    override suspend fun getLatestUnprocessedRecommendationRequestId(): UUID {
        return database.recommendationDao.getLatestUnprocessedRecommendationRequestId()
    }
}