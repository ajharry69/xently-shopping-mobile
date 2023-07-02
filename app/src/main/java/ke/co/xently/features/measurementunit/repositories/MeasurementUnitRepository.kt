package ke.co.xently.features.measurementunit.repositories

import ke.co.xently.features.measurementunit.datasources.MeasurementUnitDataSource
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeasurementUnitRepository @Inject constructor(
    private val remoteDataSource: MeasurementUnitDataSource<MeasurementUnit.RemoteRequest, MeasurementUnit.RemoteResponse>,
    private val localDataSource: MeasurementUnitDataSource<MeasurementUnit.LocalEntityRequest, MeasurementUnit.LocalEntityResponse>,
) {
    suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit): Result<List<MeasurementUnit.LocalViewModel>> {
        return try {
            localDataSource.getMeasurementUnitSearchSuggestions(query.toLocalEntityRequest())
                .ifEmpty {
                    remoteDataSource.getMeasurementUnitSearchSuggestions(query.toRemoteRequest())
                }.map { it.toLocalViewModel() }.let {
                    Result.success(it)
                }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}