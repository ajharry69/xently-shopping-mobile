package ke.co.xently.products.datasource.brands

import ke.co.xently.products.models.Brand

interface BrandDataSource<TRequest : Brand, TResponse : Brand> {
    suspend fun getBrandSearchSuggestions(query: TRequest): List<TResponse>
}