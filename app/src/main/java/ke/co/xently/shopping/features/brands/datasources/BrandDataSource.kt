package ke.co.xently.shopping.features.brands.datasources

import ke.co.xently.shopping.features.brands.models.Brand

interface BrandDataSource<TRequest : Brand, TResponse : Brand> {
    suspend fun getBrandSearchSuggestions(query: TRequest): List<TResponse>
}