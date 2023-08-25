package ke.co.xently.shopping.features.products.datasources

import ke.co.xently.shopping.features.products.datasources.remoteservices.ProductService
import ke.co.xently.shopping.features.products.models.Product
import ke.co.xently.shopping.datasource.remote.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductDataSource @Inject constructor(
    private val service: ProductService,
) : ProductDataSource<Product.RemoteRequest, Product.RemoteResponse> {
    override suspend fun addProduct(product: Product.RemoteRequest): Product.RemoteResponse {
        return SendHttpRequest {
            service.add(product)
        }.getOrThrow()
    }

    override suspend fun getProductSearchSuggestions(query: Product.RemoteRequest): List<Product.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.name.name)
        }.getOrThrow()._embedded.viewModels
    }
}