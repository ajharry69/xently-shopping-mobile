package ke.co.xently.products.datasource.measurementunit

import ke.co.xently.products.models.MeasurementUnit

interface MeasurementUnitDataSource<TRequest : MeasurementUnit, TResponse : MeasurementUnit> {
    suspend fun getMeasurementUnitSearchSuggestions(query: TRequest): List<TResponse>
}