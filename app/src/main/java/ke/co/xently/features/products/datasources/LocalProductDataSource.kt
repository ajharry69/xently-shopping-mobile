package ke.co.xently.features.products.datasources

import ke.co.xently.features.products.models.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalProductDataSource @Inject constructor() :
    ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse> {
    override suspend fun addProduct(product: Product.LocalEntityRequest): Product.LocalEntityResponse {
        return product.toLocalEntityResponse()
    }

    override suspend fun getProductSearchSuggestions(query: Product.LocalEntityRequest): List<Product.LocalEntityResponse> {
        return emptyList()
    }
}