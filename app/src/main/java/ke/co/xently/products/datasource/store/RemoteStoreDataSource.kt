package ke.co.xently.products.datasource.store

import ke.co.xently.products.datasource.remoteservices.StoreService
import ke.co.xently.products.models.Store
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteStoreDataSource @Inject constructor(
    private val service: StoreService,
) : StoreDataSource<Store.RemoteRequest, Store.RemoteResponse> {
    override suspend fun getStoreSearchSuggestions(query: Store.RemoteRequest): List<Store.RemoteResponse> {
        return List(Random.nextInt(0, 10)) {
            query.toRemoteResponse().copy(
                name = buildString { append(query.name); if (!endsWith(' ')) append(' '); append(it + 1) },
                shop = query.shop.toRemoteResponse().copy(
                    name = "Shop name ${Random.nextInt()}"
                ),
            )
        }
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()
    }
}