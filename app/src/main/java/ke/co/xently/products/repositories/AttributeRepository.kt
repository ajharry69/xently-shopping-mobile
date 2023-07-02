package ke.co.xently.products.repositories

import ke.co.xently.products.datasource.attributes.AttributeDataSource
import ke.co.xently.products.models.Attribute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttributeRepository @Inject constructor(
    private val remoteDataSource: AttributeDataSource<Attribute.RemoteRequest, Attribute.RemoteResponse>,
    private val localDataSource: AttributeDataSource<Attribute.LocalEntityRequest, Attribute.LocalEntityResponse>,
) {
    suspend fun getAttributeSearchSuggestions(query: Attribute): Result<List<Attribute.LocalViewModel>> {
        return try {
            localDataSource.getAttributeSearchSuggestions(query.toLocalEntityRequest()).ifEmpty {
                remoteDataSource.getAttributeSearchSuggestions(query.toRemoteRequest())
            }.map { it.toLocalViewModel() }.let {
                Result.success(it)
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}