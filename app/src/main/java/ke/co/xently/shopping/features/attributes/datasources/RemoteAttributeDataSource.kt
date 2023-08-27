package ke.co.xently.shopping.features.attributes.datasources

import ke.co.xently.shopping.datasource.remote.SendHttpRequest
import ke.co.xently.shopping.features.attributes.datasources.remoteservices.AttributeService
import ke.co.xently.shopping.features.attributes.models.Attribute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAttributeDataSource @Inject constructor(
    private val service: AttributeService,
) : AttributeDataSource<Attribute.RemoteRequest, Attribute.RemoteResponse> {
    override suspend fun getAttributeSearchSuggestions(query: Attribute.RemoteRequest): List<Attribute.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}