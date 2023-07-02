package ke.co.xently.features.products.datasources.remoteservices

import ke.co.xently.features.attributes.models.Attribute
import ke.co.xently.remotedatasource.CacheControl
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