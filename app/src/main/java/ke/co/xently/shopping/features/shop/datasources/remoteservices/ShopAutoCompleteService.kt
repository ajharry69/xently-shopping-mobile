package ke.co.xently.shopping.features.shop.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.datasource.remote.services.WebsocketAutoCompleteService
import ke.co.xently.shopping.features.shop.models.Shop
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ShopAutoCompleteService : AutoCompleteService<Shop> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Shop>(
            client = client,
            endpoint = "search/suggest/shops",
            queryString = Shop::name,
            mapResponse = { response ->
                decodeFromString<List<Shop.RemoteResponse>>(response.json)
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
        ), ShopAutoCompleteService

    object Fake : AutoCompleteService.Fake<Shop>(), ShopAutoCompleteService
}