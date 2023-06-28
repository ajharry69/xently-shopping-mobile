package ke.co.xently.products.datasource.store

import ke.co.xently.products.models.Store

interface StoreDataSource<TRequest : Store, TResponse : Store> {
    suspend fun getStoreSearchSuggestions(query: TRequest): List<TResponse>
}