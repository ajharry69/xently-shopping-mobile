package ke.co.xently.features.attributesvalues.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AttributeValueAutoCompleteService :
    AutoCompleteService<AttributeValue> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<AttributeValue>(
            client = client,
            endpoint = "search/suggest/attribute-values",
            queryString = { it.toString() },
            mapResponse = { response ->
                decodeFromString<List<AttributeValue.RemoteResponse>>(response.json)
                    .map { it.toLocalViewModel() }
                    .let {
                        val data = if (response.currentQuery == null) {
                            it
                        } else {
                            listOf(response.currentQuery.toLocalViewModel()) + it
                        }
                        AutoCompleteService.ResultState.Success(data)
                    }
            },
        ), AttributeValueAutoCompleteService

    object Fake : AutoCompleteService.Fake<AttributeValue>(),
        AttributeValueAutoCompleteService
}