package ke.co.xently.features.store.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.store.models.Store
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface StoreAutoCompleteService : AutoCompleteService<Store, Store> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Store, Store>(
            client = client,
            endpoint = "search/suggest/stores",
            queryString = Store::name,
        ), StoreAutoCompleteService

    object Fake : AutoCompleteService.Fake<Store, Store>(), StoreAutoCompleteService
}