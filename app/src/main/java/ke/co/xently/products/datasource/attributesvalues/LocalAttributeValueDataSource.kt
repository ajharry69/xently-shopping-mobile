package ke.co.xently.products.datasource.attributesvalues

import ke.co.xently.products.models.AttributeValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalAttributeValueDataSource @Inject constructor() :
    AttributeValueDataSource<AttributeValue.LocalEntityRequest, AttributeValue.LocalEntityResponse> {
    override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue.LocalEntityRequest): List<AttributeValue.LocalEntityResponse> {
        return emptyList()
    }
}