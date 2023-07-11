package ke.co.xently.features.brands.repositories

import ke.co.xently.features.brands.datasources.BrandDataSource
import ke.co.xently.features.brands.models.Brand
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BrandRepository {
    suspend fun getBrandSearchSuggestions(query: Brand): Result<List<Brand.LocalViewModel>>

    object Fake : BrandRepository {
        override suspend fun getBrandSearchSuggestions(query: Brand): Result<List<Brand.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: BrandDataSource<Brand.RemoteRequest, Brand.RemoteResponse>,
        private val localDataSource: BrandDataSource<Brand.LocalEntityRequest, Brand.LocalEntityResponse>,
    ) : BrandRepository {
        override suspend fun getBrandSearchSuggestions(query: Brand): Result<List<Brand.LocalViewModel>> {
            return try {
                localDataSource.getBrandSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
                    remoteDataSource.getBrandSearchSuggestions(query.toRemoteRequest())
                }.map { it.toLocalViewModel() }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}