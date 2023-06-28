package ke.co.xently.products.repositories

import android.util.Log
import ke.co.xently.products.datasource.ProductDataSource
import ke.co.xently.products.models.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val remoteDataSource: ProductDataSource<Product.RemoteRequest, Product.RemoteResponse>,
    private val localDataSource: ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse>,
) {
    suspend fun addProduct(product: Product): Result<Product.LocalViewModel> {
        Log.i(TAG, "addProduct: $product")
        return try {
            remoteDataSource.addProduct(product.toRemoteRequest()).let {
                localDataSource.addProduct(it.toLocalEntityRequest())
            }.let {
                Result.success(it.toLocalViewModel())
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun getProductSearchSuggestions(query: Product): Result<List<Product.LocalViewModel>> {
        return localDataSource.getProductSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
            remoteDataSource.getProductSearchSuggestions(query.toRemoteRequest())
        }.map { it.toLocalViewModel() }.let {
            Result.success(it)
        }
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName
    }
}