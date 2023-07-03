package ke.co.xently.features.compareproducts.repositories

import ke.co.xently.features.compareproducts.datasources.CompareProductDataSource
import ke.co.xently.features.compareproducts.models.CompareProduct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompareProductRepository @Inject constructor(
    private val remoteDataSource: CompareProductDataSource<CompareProduct.Request, CompareProduct.Response>,
) {
    suspend fun compareProducts(request: CompareProduct.Request): Result<CompareProduct.Response> {
        return try {
            remoteDataSource.compareProducts(request).let {
                Result.success(it)
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}