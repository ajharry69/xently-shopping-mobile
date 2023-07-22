package ke.co.xently.shopping.features.attributesvalues.datasources

import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalAttributeValueDataSource @Inject constructor() :
    AttributeValueDataSource<AttributeValue.LocalEntityRequest, AttributeValue.LocalEntityResponse> {
    override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue.LocalEntityRequest): List<AttributeValue.LocalEntityResponse> {
        return emptyList()
    }
}