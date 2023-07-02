package ke.co.xently.features.brands.datasources

import ke.co.xently.features.brands.datasources.remoteservices.BrandService
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteBrandDataSource @Inject constructor(
    private val service: BrandService,
) : BrandDataSource<Brand.RemoteRequest, Brand.RemoteResponse> {
    override suspend fun getBrandSearchSuggestions(query: Brand.RemoteRequest): List<Brand.RemoteResponse> {
        return List(Random(0).nextInt(5)) {
            query.toRemoteResponse().copy(name = buildString { append(query.name); append(it + 1) })
        }
        return SendHttpRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}