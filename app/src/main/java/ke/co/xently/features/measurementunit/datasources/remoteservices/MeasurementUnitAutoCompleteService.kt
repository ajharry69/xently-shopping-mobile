package ke.co.xently.features.measurementunit.datasources.remoteservices

import io.ktor.client.HttpClient
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.remotedatasource.services.AutoCompleteService
import ke.co.xently.remotedatasource.services.WebsocketAutoCompleteService
import javax.inject.Inject
import javax.inject.Singleton

sealed interface MeasurementUnitAutoCompleteService :
    AutoCompleteService<MeasurementUnit, MeasurementUnit> {
    @Singleton
    class Actual @Inject constructor(client: HttpClient) :
        WebsocketAutoCompleteService<MeasurementUnit, MeasurementUnit>(
            client = client,
            endpoint = "search/suggest/measurement-units",
            queryString = { it.name },
        ), MeasurementUnitAutoCompleteService

    object Fake : AutoCompleteService.Fake<MeasurementUnit, MeasurementUnit>(),
        MeasurementUnitAutoCompleteService
}