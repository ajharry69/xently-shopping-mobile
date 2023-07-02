package ke.co.xently.features.store.datasources

import ke.co.xently.features.store.datasources.remoteservices.StoreService
import ke.co.xently.features.store.models.Store
import ke.co.xently.remotedatasource.SendHttpRequest
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