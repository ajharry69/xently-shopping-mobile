package ke.co.xently.features.store.datasources

import ke.co.xently.features.store.models.Store

interface StoreDataSource<TRequest : Store, TResponse : Store> {
    suspend fun getStoreSearchSuggestions(query: TRequest): List<TResponse>
}