package ke.co.xently.features.compareproducts.datasources.remoteservices

import ke.co.xently.features.compareproducts.models.CompareProduct
import retrofit2.Response
import retrofit2.http.*

interface CompareProductService {
    @POST("compare-products")
    suspend fun get(@Body request: CompareProduct.Request): Response<CompareProduct.Response>
}