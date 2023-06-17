package ke.co.xently.products.datasource

import ke.co.xently.products.models.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductDataSource @Inject constructor() :
    ProductDataSource<Product.RemoteRequest, Product.RemoteResponse> {
    override suspend fun addProduct(product: Product.RemoteRequest): Product.RemoteResponse {
        return product.toRemoteResponse()
    }

    override suspend fun getProductById(id: Long): Product.RemoteResponse? {
        TODO("Not yet implemented")
    }
}