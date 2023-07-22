package ke.co.xently.shopping.features.attributesvalues.repositories

import ke.co.xently.shopping.features.attributesvalues.datasources.AttributeValueDataSource
import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AttributeValueRepository {
    suspend fun getAttributeValueSearchSuggestions(query: AttributeValue): Result<List<AttributeValue.LocalViewModel>>

    object Fake : AttributeValueRepository {
        override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue): Result<List<AttributeValue.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: AttributeValueDataSource<AttributeValue.RemoteRequest, AttributeValue.RemoteResponse>,
        private val localDataSource: AttributeValueDataSource<AttributeValue.LocalEntityRequest, AttributeValue.LocalEntityResponse>,
    ) : AttributeValueRepository {
        override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue): Result<List<AttributeValue.LocalViewModel>> {
            return try {
                localDataSource.getAttributeValueSearchSuggestions(query.toLocalEntityRequest())
                    .ifEmpty {
                        remoteDataSource.getAttributeValueSearchSuggestions(query.toRemoteRequest())
                    }.map { it.toLocalViewModel() }.let {
                        Result.success(it)
                    }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}