package ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAutoCompleteService(service: MeasurementUnitAutoCompleteService.Actual): MeasurementUnitAutoCompleteService
}