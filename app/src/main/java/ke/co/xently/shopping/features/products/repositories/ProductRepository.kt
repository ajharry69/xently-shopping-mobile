package ke.co.xently.shopping.features.products.repositories

import ke.co.xently.shopping.features.products.datasources.ProductDataSource
import ke.co.xently.shopping.features.products.models.Product
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProductRepository {
    suspend fun addProduct(product: Product): Result<Product.LocalViewModel>
    suspend fun getProductSearchSuggestions(query: Product): Result<List<Product.LocalViewModel>>

    object Fake : ProductRepository {
        override suspend fun addProduct(product: Product): Result<Product.LocalViewModel> {
            return Result.success(product.toLocalViewModel())
        }

        override suspend fun getProductSearchSuggestions(query: Product): Result<List<Product.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: ProductDataSource<Product.RemoteRequest, Product.RemoteResponse>,
        private val localDataSource: ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse>,
    ) : ProductRepository {
        override suspend fun addProduct(product: Product): Result<Product.LocalViewModel> {
            Timber.tag(TAG).i("addProduct: %s", product)
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

        override suspend fun getProductSearchSuggestions(query: Product): Result<List<Product.LocalViewModel>> {
            return try {
                localDataSource.getProductSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
                    remoteDataSource.getProductSearchSuggestions(query.toRemoteRequest())
                }.map { it.toLocalViewModel() }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }

        companion object {
            private val TAG = ProductRepository::class.java.simpleName
        }
    }
}