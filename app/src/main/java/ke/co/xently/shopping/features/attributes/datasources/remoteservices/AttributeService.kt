package ke.co.xently.shopping.features.attributes.datasources.remoteservices

import ke.co.xently.shopping.datasource.remote.CacheControl
import ke.co.xently.shopping.features.attributes.models.Attribute
import ke.co.xently.shopping.features.core.models.RemoteSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface AttributeService {
    @GET("attributes")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = CacheControl.NoCache.toString(),
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<Attribute.RemoteResponse>>
}