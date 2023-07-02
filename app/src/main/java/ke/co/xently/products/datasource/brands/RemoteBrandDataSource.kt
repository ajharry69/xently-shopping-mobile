package ke.co.xently.products.datasource.brands

import ke.co.xently.products.datasource.remoteservices.BrandService
import ke.co.xently.products.models.Brand
import ke.co.xently.remotedatasource.Http.sendRequest
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
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}