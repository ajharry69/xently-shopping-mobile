package ke.co.xently.shopping.features.store.datasources.remoteservices

import ke.co.xently.shopping.features.core.models.RemoteSearchResponse
import ke.co.xently.shopping.features.store.models.Store
import ke.co.xently.shopping.remotedatasource.CacheControl
import retrofit2.Response
import retrofit2.http.*

interface StoreService {
    @GET("stores")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = CacheControl.NoCache.toString(),
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<Store.RemoteResponse>>
}