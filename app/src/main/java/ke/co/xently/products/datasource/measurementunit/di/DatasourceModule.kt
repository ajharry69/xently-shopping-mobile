package ke.co.xently.products.datasource.measurementunit.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.measurementunit.LocalMeasurementUnitDataSource
import ke.co.xently.products.datasource.measurementunit.MeasurementUnitDataSource
import ke.co.xently.products.datasource.measurementunit.RemoteMeasurementUnitDataSource
import ke.co.xently.products.datasource.remoteservices.MeasurementUnitService
import ke.co.xently.products.models.MeasurementUnit
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): MeasurementUnitDataSource<MeasurementUnit.RemoteRequest, MeasurementUnit.RemoteResponse> {
        return RemoteMeasurementUnitDataSource(
            service = retrofit.create(MeasurementUnitService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): MeasurementUnitDataSource<MeasurementUnit.LocalEntityRequest, MeasurementUnit.LocalEntityResponse> {
        return LocalMeasurementUnitDataSource()
    }
}