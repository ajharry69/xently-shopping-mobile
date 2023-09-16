package ke.co.xently.shopping.features.store.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.datasource.remote.services.WebsocketAutoCompleteService
import ke.co.xently.shopping.features.store.models.Store
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
        queryString = { store ->
            val query = store.name
            if (query.isBlank()) {
                ""
            } else {
                var latitude: Double? = null
                var longitude: Double? = null
                if (store.location.isUsable()) {
                    latitude = store.location.latitude
                    longitude = store.location.longitude
                }
                //language=JSON
                """{"q": "$query","lat": $latitude,"lon": $longitude}"""
            }
        },
        mapResponse = { response ->
            decodeFromString<List<Store.RemoteResponse>>(response.json)
                .map { it.toLocalViewModel() }
                .let {
                    val data = if (response.currentQuery == null) {
                        it
                    } else {
                        val strings = it.map { i ->
                            i.toString()
                                .replace("\\s+".toRegex(), "")
                                .lowercase()
                        }

                        val item = response.currentQuery.toLocalViewModel()
                        val itemString =
                            item.toString().replace("\\s+".toRegex(), "").lowercase()

                        if (itemString in strings) {
                            it
                        } else {
                            listOf(item) + it
                        }
                    }
                    AutoCompleteService.ResultState.Success(data)
                }
        },
    ), StoreAutoCompleteService

    object Fake : AutoCompleteService.Fake<Store>(), StoreAutoCompleteService
}