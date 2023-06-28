package ke.co.xently.products.repositories

import ke.co.xently.products.datasource.shop.ShopDataSource
import ke.co.xently.products.models.Shop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val remoteDataSource: ShopDataSource<Shop.RemoteRequest, Shop.RemoteResponse>,
    private val localDataSource: ShopDataSource<Shop.LocalEntityRequest, Shop.LocalEntityResponse>,
) {
    suspend fun getShopSearchSuggestions(query: Shop): Result<List<Shop.LocalViewModel>> {
        return localDataSource.getShopSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
            remoteDataSource.getShopSearchSuggestions(query.toRemoteRequest())
        }.map { it.toLocalViewModel() }.let {
            Result.success(it)
        }
    }
}