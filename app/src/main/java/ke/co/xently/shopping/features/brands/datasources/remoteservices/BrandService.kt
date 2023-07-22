package ke.co.xently.shopping.features.brands.datasources.remoteservices

import ke.co.xently.shopping.features.brands.models.Brand
import ke.co.xently.shopping.features.core.models.RemoteSearchResponse
import ke.co.xently.shopping.remotedatasource.CacheControl
import retrofit2.Response
import retrofit2.http.*

interface BrandService {
    @GET("brands")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = CacheControl.NoCache.toString(),
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<Brand.RemoteResponse>>
}