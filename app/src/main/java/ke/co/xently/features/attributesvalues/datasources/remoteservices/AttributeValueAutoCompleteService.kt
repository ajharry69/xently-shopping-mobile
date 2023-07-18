package ke.co.xently.features.attributesvalues.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AttributeValueAutoCompleteService :
    AutoCompleteService<AttributeValue, AttributeValue> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<AttributeValue, AttributeValue>(
            client = client,
            endpoint = "search/suggest/attribute-values",
            queryString = { it.toString() },
        ), AttributeValueAutoCompleteService

    object Fake : AutoCompleteService.Fake<AttributeValue, AttributeValue>(),
        AttributeValueAutoCompleteService
}