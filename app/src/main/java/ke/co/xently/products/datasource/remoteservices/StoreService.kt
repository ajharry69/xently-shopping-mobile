package ke.co.xently.products.datasource.remoteservices

import ke.co.xently.products.models.Store
import retrofit2.Response
import retrofit2.http.*

interface StoreService {
    @GET("stores/")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = "only-if-cached",
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<List<Store.RemoteResponse>>
}