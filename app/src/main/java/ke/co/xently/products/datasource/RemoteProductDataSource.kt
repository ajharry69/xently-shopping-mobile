package ke.co.xently.products.datasource

import ke.co.xently.products.datasource.remoteservices.ProductService
import ke.co.xently.products.models.Product
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteProductDataSource @Inject constructor(
    private val service: ProductService,
) : ProductDataSource<Product.RemoteRequest, Product.RemoteResponse> {
    override suspend fun addProduct(product: Product.RemoteRequest): Product.RemoteResponse {
        return sendRequest {
            service.add(product)
        }.getOrThrow()
    }

    override suspend fun getProductSearchSuggestions(query: Product.RemoteRequest): List<Product.RemoteResponse> {
        return List(Random.nextInt(0, 10)) {
            query.toRemoteResponse().run {
                copy(name = name.run {
                    copy(name = name.plus(it + 1))
                })
            }
        }
        return sendRequest {
            service.searchSuggestions(query = query.name.name)
        }.getOrThrow()
    }
}