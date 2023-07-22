package ke.co.xently.shopping.features.attributesvalues.datasources

import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue

interface AttributeValueDataSource<TRequest : AttributeValue, TResponse : AttributeValue> {
    suspend fun getAttributeValueSearchSuggestions(query: TRequest): List<TResponse>
}