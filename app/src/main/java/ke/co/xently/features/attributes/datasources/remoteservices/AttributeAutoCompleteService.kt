package ke.co.xently.features.attributes.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.attributes.models.Attribute
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AttributeAutoCompleteService : AutoCompleteService<Attribute, Attribute> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Attribute, Attribute>(
            client = client,
            endpoint = "search/suggest/attributes",
            queryString = { it.name },
        ), AttributeAutoCompleteService

    object Fake : AutoCompleteService.Fake<Attribute, Attribute>(), AttributeAutoCompleteService
}