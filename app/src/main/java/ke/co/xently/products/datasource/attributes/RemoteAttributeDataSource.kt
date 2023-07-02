package ke.co.xently.products.datasource.attributes

import ke.co.xently.products.datasource.remoteservices.AttributeService
import ke.co.xently.products.models.Attribute
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteAttributeDataSource @Inject constructor(
    private val service: AttributeService,
) : AttributeDataSource<Attribute.RemoteRequest, Attribute.RemoteResponse> {
    override suspend fun getAttributeSearchSuggestions(query: Attribute.RemoteRequest): List<Attribute.RemoteResponse> {
        return List(Random(0).nextInt(5)) {
            query.toRemoteResponse()
                .copy(name = buildString { append(query.name); append(it + 1) })
        }
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}