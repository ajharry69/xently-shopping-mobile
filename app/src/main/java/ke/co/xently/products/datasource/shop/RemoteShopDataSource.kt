package ke.co.xently.products.datasource.shop

import ke.co.xently.products.datasource.remoteservices.ShopService
import ke.co.xently.products.models.Shop
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteShopDataSource @Inject constructor(
    private val service: ShopService,
) : ShopDataSource<Shop.RemoteRequest, Shop.RemoteResponse> {
    override suspend fun getShopSearchSuggestions(query: Shop.RemoteRequest): List<Shop.RemoteResponse> {
        return  List(Random.nextInt(0, 10)) {
            query.toRemoteResponse().copy(
                name = buildString { append(query.name); if (!endsWith(' ')) append(' '); append(it + 1) },
            )
        }
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()
    }
}