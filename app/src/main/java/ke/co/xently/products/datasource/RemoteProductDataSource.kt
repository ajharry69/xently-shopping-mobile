package ke.co.xently.products.datasource

import ke.co.xently.products.datasource.remoteservices.ProductService
import ke.co.xently.products.models.Product
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductDataSource @Inject constructor(
    private val service: ProductService,
) : ProductDataSource<Product.RemoteRequest, Product.RemoteResponse> {
    override suspend fun addProduct(product: Product.RemoteRequest): Product.RemoteResponse {
        return sendRequest {
            service.add(product)
        }.getOrThrow()
    }

    override suspend fun getProductById(id: Long): Product.RemoteResponse? {
        TODO("Not yet implemented")
    }
}