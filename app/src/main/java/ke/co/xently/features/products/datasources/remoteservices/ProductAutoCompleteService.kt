package ke.co.xently.features.products.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.products.models.Product
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProductAutoCompleteService : AutoCompleteService<Product> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Product>(
            client = client,
            endpoint = "search/suggest/products",
            queryString = { it.name.name },
        ), ProductAutoCompleteService

    object Fake : AutoCompleteService.Fake<Product>(), ProductAutoCompleteService
}