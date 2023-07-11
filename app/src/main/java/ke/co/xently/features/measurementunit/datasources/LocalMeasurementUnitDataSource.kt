package ke.co.xently.features.measurementunit.datasources

import ke.co.xently.features.measurementunit.models.MeasurementUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalMeasurementUnitDataSource @Inject constructor() :
    MeasurementUnitDataSource<MeasurementUnit.LocalEntityRequest, MeasurementUnit.LocalEntityResponse> {
    override suspend fun getMeasurementUnitSearchSuggestions(query: MeasurementUnit.LocalEntityRequest): List<MeasurementUnit.LocalEntityResponse> {
        return emptyList()
    }
}