package ke.co.xently.features.measurementunit.datasources

import ke.co.xently.features.measurementunit.datasources.remoteservices.MeasurementUnitService
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.remotedatasource.SendHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMeasurementUnitDataSource @Inject constructor(
    private val service: MeasurementUnitService,
) : MeasurementUnitDataSource<MeasurementUnit.RemoteRequest, MeasurementUnit.RemoteResponse> {
    override suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit.RemoteRequest): List<MeasurementUnit.RemoteResponse> {
        return SendHttpRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()._embedded.viewModels
    }
}