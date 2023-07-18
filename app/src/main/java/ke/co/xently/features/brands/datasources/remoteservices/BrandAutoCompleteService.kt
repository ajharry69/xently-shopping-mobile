package ke.co.xently.features.brands.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BrandAutoCompleteService : AutoCompleteService<Brand, Brand> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<Brand, Brand>(
            client = client,
            endpoint = "search/suggest/brands",
            queryString = { it.name },
        ), BrandAutoCompleteService

    object Fake : AutoCompleteService.Fake<Brand, Brand>(), BrandAutoCompleteService
}