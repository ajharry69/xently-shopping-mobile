package ke.co.xently.features.brands.datasources

import ke.co.xently.features.brands.models.Brand

interface BrandDataSource<TRequest : Brand, TResponse : Brand> {
    suspend fun getBrandSearchSuggestions(query: TRequest): List<TResponse>
}