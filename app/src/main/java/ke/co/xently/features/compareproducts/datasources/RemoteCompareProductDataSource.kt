package ke.co.xently.features.compareproducts.datasources

import ke.co.xently.features.compareproducts.datasources.remoteservices.CompareProductService
import ke.co.xently.features.compareproducts.models.CompareProduct
import ke.co.xently.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteCompareProductDataSource @Inject constructor(
    private val service: CompareProductService,
) :
    CompareProductDataSource<CompareProduct.Request, CompareProduct.Response> {
    override suspend fun getCompareProducts(request: CompareProduct.Request): CompareProduct.Response {
        return SendHttpRequest {
            service.get(request)
        }.getOrThrow()
    }
}