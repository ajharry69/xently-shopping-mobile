package ke.co.xently.products.datasource.store

import ke.co.xently.products.datasource.remoteservices.StoreService
import ke.co.xently.products.models.Store
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteStoreDataSource @Inject constructor(
    private val service: StoreService,
) : StoreDataSource<Store.RemoteRequest, Store.RemoteResponse> {
    override suspend fun getStoreSearchSuggestions(query: Store.RemoteRequest): List<Store.RemoteResponse> {
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}