package ke.co.xently.shopping.features.store.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.features.store.models.Store
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.datasource.remote.services.WebsocketAutoCompleteService
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
            decodeFromString<List<Store.RemoteResponse>>(response.json)
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
    ), StoreAutoCompleteService

    object Fake : AutoCompleteService.Fake<Store>(), StoreAutoCompleteService
}