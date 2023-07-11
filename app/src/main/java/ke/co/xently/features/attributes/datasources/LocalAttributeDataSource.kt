package ke.co.xently.features.attributes.datasources

import ke.co.xently.features.attributes.models.Attribute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalAttributeDataSource @Inject constructor() :
    AttributeDataSource<Attribute.LocalEntityRequest, Attribute.LocalEntityResponse> {
    override suspend fun getAttributeSearchSuggestions(query: Attribute.LocalEntityRequest): List<Attribute.LocalEntityResponse> {
        return emptyList()
    }
}