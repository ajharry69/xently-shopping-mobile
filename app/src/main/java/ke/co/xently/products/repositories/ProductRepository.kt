package ke.co.xently.products.repositories

import android.util.Log
import ke.co.xently.products.datasource.ProductDataSource
import ke.co.xently.products.exceptions.ProductNotFoundException
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
        return remoteDataSource.addProduct(product.toRemoteRequest()).let {
            localDataSource.addProduct(it.toLocalEntityRequest())
        }.let {
            Result.success(it.toLocalViewModel())
        }
    }

    suspend fun getProductById(id: Long): Result<Product.LocalViewModel> {
        return (localDataSource.getProductById(id)?.toLocalViewModel()
            ?: remoteDataSource.getProductById(id)?.let {
                localDataSource.addProduct(it.toLocalEntityRequest()).toLocalViewModel()
            }
                )?.let { Result.success(it) }
            ?: Result.failure(ProductNotFoundException("""Product with ID "$id" could not be found!"""))
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName
    }
}