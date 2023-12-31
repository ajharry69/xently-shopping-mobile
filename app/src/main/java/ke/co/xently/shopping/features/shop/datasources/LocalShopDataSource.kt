package ke.co.xently.shopping.features.shop.datasources

import ke.co.xently.shopping.features.shop.models.Shop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalShopDataSource @Inject constructor() :
    ShopDataSource<Shop.LocalEntityRequest, Shop.LocalEntityResponse> {
    override suspend fun getShopSearchSuggestions(query: Shop.LocalEntityRequest): List<Shop.LocalEntityResponse> {
        return emptyList()
    }
}