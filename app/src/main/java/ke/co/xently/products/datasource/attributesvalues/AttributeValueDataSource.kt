package ke.co.xently.products.datasource.attributesvalues

import ke.co.xently.products.models.AttributeValue

interface AttributeValueDataSource<TRequest : AttributeValue, TResponse : AttributeValue> {
    suspend fun getAttributeValueSearchSuggestions(query: TRequest): List<TResponse>
}