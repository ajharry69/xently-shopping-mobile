package ke.co.xently.features.shop.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.shop.models.Shop
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ShopAutoCompleteService : AutoCompleteService<Shop> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Shop>(
            client = client,
            endpoint = "search/suggest/shops",
            queryString = Shop::name,
        ), ShopAutoCompleteService

    object Fake : AutoCompleteService.Fake<Shop>(), ShopAutoCompleteService
}