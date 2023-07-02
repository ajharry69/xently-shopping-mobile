package ke.co.xently.features.shop.datasources

import ke.co.xently.features.products.datasources.remoteservices.ShopService
import ke.co.xently.features.shop.models.Shop
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteShopDataSource @Inject constructor(
    private val service: ShopService,
) : ShopDataSource<Shop.RemoteRequest, Shop.RemoteResponse> {
    override suspend fun getShopSearchSuggestions(query: Shop.RemoteRequest): List<Shop.RemoteResponse> {
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}