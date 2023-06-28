package ke.co.xently.products.datasource.measurementunit

import ke.co.xently.products.datasource.remoteservices.MeasurementUnitService
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.remotedatasource.Http.sendRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteMeasurementUnitDataSource @Inject constructor(
    private val service: MeasurementUnitService,
) : MeasurementUnitDataSource<MeasurementUnit.RemoteRequest, MeasurementUnit.RemoteResponse> {
    override suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit.RemoteRequest): List<MeasurementUnit.RemoteResponse> {
        return List(Random.nextInt(0, 10)) {
            val name = buildString {
                append(query.name)
                if (!endsWith(' ')) {
                    append(' ')
                }
                append(it + 1)
            }
            query.toRemoteResponse().copy(name = name)
        }
        return sendRequest {
            service.searchSuggestions(query = query.name)
        }.getOrThrow()
    }
}