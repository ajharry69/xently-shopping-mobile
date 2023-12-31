package ke.co.xently.shopping.features.products.datasources.remoteservices

import ke.co.xently.shopping.features.core.models.RemoteSearchResponse
import ke.co.xently.shopping.features.products.models.Product
import retrofit2.Response
import retrofit2.http.*

interface ProductService {
    @POST("products")
    suspend fun add(@Body product: Product.RemoteRequest): Response<Product.RemoteResponse>

    @GET("products")
    suspend fun searchSuggestions(
        @Query("q")
        query: String,
        @Query("size")
        size: Int = 5,
        @Header("Cache-Control")
        cacheControl: String = "only-if-cached",
        @QueryMap
        queries: Map<String, String> = emptyMap(),
    ): Response<RemoteSearchResponse<Product.RemoteResponse>>
}