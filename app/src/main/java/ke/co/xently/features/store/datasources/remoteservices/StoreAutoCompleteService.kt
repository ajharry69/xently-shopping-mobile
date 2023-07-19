package ke.co.xently.features.store.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.store.models.Store
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface StoreAutoCompleteService : AutoCompleteService<Store> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) : WebsocketAutoCompleteService<Store>(
        client = client,
        endpoint = "search/suggest/stores",
        queryString = Store::name,
        mapResponse = { response ->
            decodeFromString<List<Store.RemoteResponse>>(response).let {
                AutoCompleteService.ResultState.Success(it)
            }
        },
    ), StoreAutoCompleteService

    object Fake : AutoCompleteService.Fake<Store>(), StoreAutoCompleteService
}