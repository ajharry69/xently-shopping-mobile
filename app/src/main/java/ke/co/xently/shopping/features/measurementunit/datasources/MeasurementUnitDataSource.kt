package ke.co.xently.shopping.features.measurementunit.datasources

import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit

interface MeasurementUnitDataSource<TRequest : MeasurementUnit, TResponse : MeasurementUnit> {
    suspend fun getMeasurementUnitSearchSuggestions(query: TRequest): List<TResponse>
}