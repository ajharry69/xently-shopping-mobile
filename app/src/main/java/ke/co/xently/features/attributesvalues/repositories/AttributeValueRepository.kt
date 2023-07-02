package ke.co.xently.features.attributesvalues.repositories

import ke.co.xently.features.attributesvalues.datasources.AttributeValueDataSource
import ke.co.xently.features.attributesvalues.models.AttributeValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttributeValueRepository @Inject constructor(
    private val remoteDataSource: AttributeValueDataSource<AttributeValue.RemoteRequest, AttributeValue.RemoteResponse>,
    private val localDataSource: AttributeValueDataSource<AttributeValue.LocalEntityRequest, AttributeValue.LocalEntityResponse>,
) {
    suspend fun getAttributeValueSearchSuggestions(query: AttributeValue): Result<List<AttributeValue.LocalViewModel>> {
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