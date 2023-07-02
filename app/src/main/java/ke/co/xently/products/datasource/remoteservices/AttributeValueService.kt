package ke.co.xently.products.datasource.remoteservices

import ke.co.xently.products.models.AttributeValue
import ke.co.xently.remotedatasource.CacheControl
import retrofit2.Response
import retrofit2.http.*

interface AttributeValueService {
    @GET("attribute-values")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = CacheControl.NoCache.toString(),
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<AttributeValue.RemoteResponse>>
}