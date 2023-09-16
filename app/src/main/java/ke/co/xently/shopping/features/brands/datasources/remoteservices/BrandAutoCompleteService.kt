package ke.co.xently.shopping.features.brands.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.datasource.remote.services.AutoCompleteService
import ke.co.xently.shopping.datasource.remote.services.WebsocketAutoCompleteService
import ke.co.xently.shopping.features.brands.models.Brand
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BrandAutoCompleteService : AutoCompleteService<Brand> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Brand>(
            client = client,
            endpoint = "search/suggest/brands",
            queryString = { it.name },
            mapResponse = { response ->
                decodeFromString<List<Brand.RemoteResponse>>(response.json)
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
        ), BrandAutoCompleteService

    object Fake : AutoCompleteService.Fake<Brand>(), BrandAutoCompleteService
}