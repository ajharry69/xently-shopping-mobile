package ke.co.xently.features.attributes.datasources

import ke.co.xently.features.attributes.models.Attribute

interface AttributeDataSource<TRequest : Attribute, TResponse : Attribute> {
    suspend fun getAttributeSearchSuggestions(query: TRequest): List<TResponse>
}