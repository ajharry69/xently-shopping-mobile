package ke.co.xently.products.datasource.shop

import ke.co.xently.products.models.Shop

interface ShopDataSource<TRequest : Shop, TResponse : Shop> {
    suspend fun getShopSearchSuggestions(query: TRequest): List<TResponse>
}