package ke.co.xently.features.compareproducts.repositories

import ke.co.xently.features.compareproducts.datasources.CompareProductDataSource
import ke.co.xently.features.compareproducts.models.CompareProduct
import javax.inject.Inject
import javax.inject.Singleton

sealed interface CompareProductRepository {
    suspend fun compareProducts(request: CompareProduct.Request): Result<CompareProduct.Response>

    object Fake : CompareProductRepository {
        override suspend fun compareProducts(request: CompareProduct.Request): Result<CompareProduct.Response> {
            return Result.success(CompareProduct.Response(emptyList()))
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: CompareProductDataSource<CompareProduct.Request, CompareProduct.Response>,
    ) : CompareProductRepository {
        override suspend fun compareProducts(request: CompareProduct.Request) = try {
            remoteDataSource.compareProducts(request).let {
                Result.success(it)
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}