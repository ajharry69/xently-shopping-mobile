package ke.co.xently.shopping.features.store.datasources

import ke.co.xently.shopping.features.store.models.Store

interface StoreDataSource<TRequest : Store, TResponse : Store> {
    suspend fun getStoreSearchSuggestions(query: TRequest): List<TResponse>
}