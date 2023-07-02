package ke.co.xently.features.measurementunit.datasources

import ke.co.xently.features.measurementunit.models.MeasurementUnit

interface MeasurementUnitDataSource<TRequest : MeasurementUnit, TResponse : MeasurementUnit> {
    suspend fun getMeasurementUnitSearchSuggestions(query: TRequest): List<TResponse>
}