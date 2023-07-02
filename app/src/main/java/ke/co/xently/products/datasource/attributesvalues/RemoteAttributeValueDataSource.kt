package ke.co.xently.products.datasource.attributesvalues

import ke.co.xently.products.datasource.remoteservices.AttributeValueService
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteAttributeValueDataSource @Inject constructor(
    private val service: AttributeValueService,
) : AttributeValueDataSource<AttributeValue.RemoteRequest, AttributeValue.RemoteResponse> {
    override suspend fun getAttributeValueSearchSuggestions(query: AttributeValue.RemoteRequest): List<AttributeValue.RemoteResponse> {
        return List(Random(0).nextInt(5)) {
            query.toRemoteResponse()
                .copy(value = buildString { append(query.value); append(it + 1) })
        }
        return sendRequest {
            service.searchSuggestions(query = query.toString())
        }.getOrThrow()._embedded.viewModels
    }
}