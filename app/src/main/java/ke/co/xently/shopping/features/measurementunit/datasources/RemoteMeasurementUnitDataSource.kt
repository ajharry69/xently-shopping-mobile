package ke.co.xently.shopping.features.measurementunit.datasources

import ke.co.xently.shopping.datasource.remote.SendHttpRequest
import ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.MeasurementUnitService
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
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