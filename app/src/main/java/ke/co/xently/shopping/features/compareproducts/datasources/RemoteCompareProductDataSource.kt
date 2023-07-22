package ke.co.xently.shopping.features.compareproducts.datasources

import ke.co.xently.shopping.features.compareproducts.datasources.remoteservices.CompareProductService
import ke.co.xently.shopping.features.compareproducts.models.CompareProduct
import ke.co.xently.shopping.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteCompareProductDataSource @Inject constructor(
    private val service: CompareProductService,
) :
    CompareProductDataSource<CompareProduct.Request, CompareProduct.Response> {
    override suspend fun compareProducts(request: CompareProduct.Request): CompareProduct.Response {
        return SendHttpRequest {
            service.get(request)
        }.getOrThrow()
    }
}