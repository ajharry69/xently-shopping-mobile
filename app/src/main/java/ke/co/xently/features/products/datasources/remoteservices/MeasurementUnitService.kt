package ke.co.xently.features.products.datasources.remoteservices

import ke.co.xently.features.measurementunit.models.MeasurementUnit
import retrofit2.Response
import retrofit2.http.*

interface MeasurementUnitService {
    @GET("measurement-units")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = "only-if-cached",
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<MeasurementUnit.RemoteResponse>>
}