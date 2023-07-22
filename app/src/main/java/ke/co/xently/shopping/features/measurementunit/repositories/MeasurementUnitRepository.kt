package ke.co.xently.shopping.features.measurementunit.repositories

import ke.co.xently.shopping.features.measurementunit.datasources.MeasurementUnitDataSource
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed interface MeasurementUnitRepository {
    suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit): Result<List<MeasurementUnit.LocalViewModel>>

    object Fake : MeasurementUnitRepository {
        override suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit): Result<List<MeasurementUnit.LocalViewModel>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: MeasurementUnitDataSource<MeasurementUnit.RemoteRequest, MeasurementUnit.RemoteResponse>,
        private val localDataSource: MeasurementUnitDataSource<MeasurementUnit.LocalEntityRequest, MeasurementUnit.LocalEntityResponse>,
    ) : MeasurementUnitRepository {
        override suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit) = try {
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