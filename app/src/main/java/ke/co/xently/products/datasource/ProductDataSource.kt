package ke.co.xently.products.datasource

import ke.co.xently.products.models.Product

interface ProductDataSource<TRequest: Product, TResponse: Product> {
    suspend fun addProduct(product: TRequest): TResponse
    suspend fun getProductById(id: Long): TResponse?
}