package ke.co.xently.shopping.features.shop.datasources

import ke.co.xently.shopping.features.shop.models.Shop

interface ShopDataSource<TRequest : Shop, TResponse : Shop> {
    suspend fun getShopSearchSuggestions(query: TRequest): List<TResponse>
}