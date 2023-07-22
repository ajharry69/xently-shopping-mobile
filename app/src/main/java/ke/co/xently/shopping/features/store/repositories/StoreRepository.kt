package ke.co.xently.shopping.features.store.repositories

import ke.co.xently.shopping.features.store.datasources.StoreDataSource
import ke.co.xently.shopping.features.store.models.Store
import javax.inject.Inject
import javax.inject.Singleton


sealed interface StoreRepository {
    suspend fun getStoreSearchSuggestions(query: Store): Result<List<Store.LocalViewModel>>

    object Fake : StoreRepository {
        override suspend fun getStoreSearchSuggestions(query: Store): Result<List<Store.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: StoreDataSource<Store.RemoteRequest, Store.RemoteResponse>,
        private val localDataSource: StoreDataSource<Store.LocalEntityRequest, Store.LocalEntityResponse>,
    ) : StoreRepository {
        override suspend fun getStoreSearchSuggestions(query: Store): Result<List<Store.LocalViewModel>> {
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
}