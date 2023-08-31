package ke.co.xently.shopping.features.store.datasources

import ke.co.xently.shopping.datasource.remote.SendHttpRequest
import ke.co.xently.shopping.features.store.datasources.remoteservices.StoreService
import ke.co.xently.shopping.features.store.models.Store
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteStoreDataSource @Inject constructor(
    private val service: StoreService,
) : StoreDataSource<Store.RemoteRequest, Store.RemoteResponse> {
    override suspend fun getStoreSearchSuggestions(query: Store.RemoteRequest): List<Store.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}