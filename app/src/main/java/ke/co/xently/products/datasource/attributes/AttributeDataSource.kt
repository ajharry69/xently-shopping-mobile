package ke.co.xently.products.datasource.attributes

import ke.co.xently.products.models.Attribute

interface AttributeDataSource<TRequest : Attribute, TResponse : Attribute> {
    suspend fun getAttributeSearchSuggestions(query: TRequest): List<TResponse>
}