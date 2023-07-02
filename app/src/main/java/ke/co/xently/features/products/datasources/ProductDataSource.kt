package ke.co.xently.features.products.datasources

import ke.co.xently.features.products.models.Product

interface ProductDataSource<TRequest : Product, TResponse : Product> {
    suspend fun addProduct(product: TRequest): TResponse
    suspend fun getProductSearchSuggestions(query: TRequest): List<TResponse>
}