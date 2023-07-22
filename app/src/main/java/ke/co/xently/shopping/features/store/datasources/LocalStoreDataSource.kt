package ke.co.xently.shopping.features.store.datasources

import ke.co.xently.shopping.features.store.models.Store
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalStoreDataSource @Inject constructor() :
    StoreDataSource<Store.LocalEntityRequest, Store.LocalEntityResponse> {
    override suspend fun getStoreSearchSuggestions(query: Store.LocalEntityRequest): List<Store.LocalEntityResponse> {
        return emptyList()
    }
}