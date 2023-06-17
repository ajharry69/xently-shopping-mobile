package ke.co.xently.products.datasource

import ke.co.xently.products.models.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalProductDataSource @Inject constructor() :
    ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse> {
    override suspend fun addProduct(product: Product.LocalEntityRequest): Product.LocalEntityResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(id: Long): Product.LocalEntityResponse? {
        TODO("Not yet implemented")
    }
}