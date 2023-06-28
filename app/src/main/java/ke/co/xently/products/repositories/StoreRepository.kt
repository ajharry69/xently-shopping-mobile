package ke.co.xently.products.repositories

import ke.co.xently.products.datasource.store.StoreDataSource
import ke.co.xently.products.models.Store
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val remoteDataSource: StoreDataSource<Store.RemoteRequest, Store.RemoteResponse>,
    private val localDataSource: StoreDataSource<Store.LocalEntityRequest, Store.LocalEntityResponse>,
) {
    suspend fun getStoreSearchSuggestions(query: Store): Result<List<Store.LocalViewModel>> {
        return try {
            localDataSource.getStoreSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
                remoteDataSource.getStoreSearchSuggestions(query.toRemoteRequest())
            }.map { it.toLocalViewModel() }.let {
                Result.success(it)
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}