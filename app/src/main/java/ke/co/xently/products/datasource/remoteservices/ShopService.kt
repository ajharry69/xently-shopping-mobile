package ke.co.xently.products.datasource.remoteservices

import ke.co.xently.products.models.Shop
import retrofit2.Response
import retrofit2.http.*

interface ShopService {
    @GET("shops")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = "only-if-cached",
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<Shop.RemoteResponse>>
}