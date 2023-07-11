package ke.co.xently.features.brands.datasources

import ke.co.xently.features.brands.models.Brand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalBrandDataSource @Inject constructor() :
    BrandDataSource<Brand.LocalEntityRequest, Brand.LocalEntityResponse> {
    override suspend fun getBrandSearchSuggestions(query: Brand.LocalEntityRequest): List<Brand.LocalEntityResponse> {
        return emptyList()
    }
}