package ke.co.xently.features.brands.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
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
                            listOf(response.currentQuery.toLocalViewModel()) + it
                        }
                        AutoCompleteService.ResultState.Success(data)
                    }
            },
        ), BrandAutoCompleteService

    object Fake : AutoCompleteService.Fake<Brand>(), BrandAutoCompleteService
}