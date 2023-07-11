package ke.co.xently.features.shop.repositories

import ke.co.xently.features.shop.datasources.ShopDataSource
import ke.co.xently.features.shop.models.Shop
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ShopRepository {
    suspend fun getShopSearchSuggestions(query: Shop): Result<List<Shop.LocalViewModel>>

    object Fake : ShopRepository {
        override suspend fun getShopSearchSuggestions(query: Shop): Result<List<Shop.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: ShopDataSource<Shop.RemoteRequest, Shop.RemoteResponse>,
        private val localDataSource: ShopDataSource<Shop.LocalEntityRequest, Shop.LocalEntityResponse>,
    ) : ShopRepository {
        override suspend fun getShopSearchSuggestions(query: Shop): Result<List<Shop.LocalViewModel>> {
            return try {
                localDataSource.getShopSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
                    remoteDataSource.getShopSearchSuggestions(query.toRemoteRequest())
                }.map { it.toLocalViewModel() }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}