package ke.co.xently.shopping.features.products.datasources

import ke.co.xently.shopping.features.products.models.Product

interface ProductDataSource<TRequest : Product, TResponse : Product> {
    suspend fun addProduct(product: TRequest): TResponse
    suspend fun getProductSearchSuggestions(query: TRequest): List<TResponse>
}