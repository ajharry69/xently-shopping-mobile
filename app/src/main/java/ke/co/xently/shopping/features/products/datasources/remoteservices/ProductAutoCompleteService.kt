package ke.co.xently.shopping.features.products.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.features.products.models.Product
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.datasource.remote.services.WebsocketAutoCompleteService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProductAutoCompleteService : AutoCompleteService<Product> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Product>(
            client = client,
            endpoint = "search/suggest/products",
            queryString = { it.name.name },
            mapResponse = { response ->
                decodeFromString<List<Product.RemoteResponse>>(response.json)
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
        ), ProductAutoCompleteService

    object Fake : AutoCompleteService.Fake<Product>(), ProductAutoCompleteService
}