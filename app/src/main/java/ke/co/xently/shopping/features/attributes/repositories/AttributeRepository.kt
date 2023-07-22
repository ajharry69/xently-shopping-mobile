package ke.co.xently.shopping.features.attributes.repositories

import ke.co.xently.shopping.features.attributes.datasources.AttributeDataSource
import ke.co.xently.shopping.features.attributes.models.Attribute
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AttributeRepository {
    suspend fun getAttributeSearchSuggestions(query: Attribute): Result<List<Attribute.LocalViewModel>>

    object Fake : AttributeRepository {
        override suspend fun getAttributeSearchSuggestions(query: Attribute): Result<List<Attribute.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: AttributeDataSource<Attribute.RemoteRequest, Attribute.RemoteResponse>,
        private val localDataSource: AttributeDataSource<Attribute.LocalEntityRequest, Attribute.LocalEntityResponse>,
    ) : AttributeRepository {
        override suspend fun getAttributeSearchSuggestions(query: Attribute): Result<List<Attribute.LocalViewModel>> {
            return try {
                localDataSource.getAttributeSearchSuggestions(query.toLocalEntityRequest())
                    .ifEmpty {
                        remoteDataSource.getAttributeSearchSuggestions(query.toRemoteRequest())
                    }.map { it.toLocalViewModel() }.let {
                        Result.success(it)
                    }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}