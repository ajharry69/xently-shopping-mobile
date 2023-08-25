package ke.co.xently.shopping.features.collectpayments.repositories

import ke.co.xently.shopping.features.collectpayments.datasources.MpesaPaymentDataSource
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed interface MpesaPaymentRepository {
    suspend fun pay(request: MpesaPaymentRequest): Result<Unit>

    object Fake : MpesaPaymentRepository {
        override suspend fun pay(request: MpesaPaymentRequest): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Singleton
    class Actual @Inject constructor(
        @Named("localMpesaPaymentDataSource")
        private val localDataSource: MpesaPaymentDataSource,
        @Named("remoteMpesaPaymentDataSource")
        private val remoteDataSource: MpesaPaymentDataSource,
    ) : MpesaPaymentRepository {
        override suspend fun pay(request: MpesaPaymentRequest): Result<Unit> {
            return try {
                val recommendationRequestId =
                    localDataSource.getLatestUnprocessedRecommendationRequestId()
                remoteDataSource.pay(
                    request = request.copy(
                        recommendationRequestId = recommendationRequestId,
                    ),
                    authorizationToken = localDataSource.getAuthorizationToken(),
                )
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}
