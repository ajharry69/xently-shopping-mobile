package ke.co.xently.shopping.features.attributesvalues.datasources

import ke.co.xently.shopping.features.attributesvalues.datasources.remoteservices.AttributeValueService
import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue
import ke.co.xently.shopping.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAttributeValueDataSource @Inject constructor(
    private val service: AttributeValueService,
) : AttributeValueDataSource<AttributeValue.RemoteRequest, AttributeValue.RemoteResponse> {
    override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue.RemoteRequest): List<AttributeValue.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.toString())
        }.getOrThrow()._embedded.viewModels
    }
}