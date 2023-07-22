package ke.co.xently.shopping.features.measurementunit.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
import ke.co.xently.shopping.remotedatasource.services.AutoCompleteService
import ke.co.xently.shopping.remotedatasource.services.WebsocketAutoCompleteService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

sealed interface MeasurementUnitAutoCompleteService : AutoCompleteService<MeasurementUnit> {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<MeasurementUnit>(
            client = client,
            endpoint = "search/suggest/measurement-units",
            queryString = { it.name },
            mapResponse = { response ->
                decodeFromString<List<MeasurementUnit.RemoteResponse>>(response.json)
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
        ), MeasurementUnitAutoCompleteService

    object Fake : AutoCompleteService.Fake<MeasurementUnit>(),
        MeasurementUnitAutoCompleteService
}