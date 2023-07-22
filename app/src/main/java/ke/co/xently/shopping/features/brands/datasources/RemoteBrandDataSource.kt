package ke.co.xently.shopping.features.brands.datasources

import ke.co.xently.shopping.features.brands.datasources.remoteservices.BrandService
import ke.co.xently.shopping.features.brands.models.Brand
import ke.co.xently.shopping.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteBrandDataSource @Inject constructor(
    private val service: BrandService,
) : BrandDataSource<Brand.RemoteRequest, Brand.RemoteResponse> {
    override suspend fun getBrandSearchSuggestions(query: Brand.RemoteRequest): List<Brand.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}