package ke.co.xently.shopping.features.measurementunit.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.measurementunit.datasources.LocalMeasurementUnitDataSource
import ke.co.xently.shopping.features.measurementunit.datasources.MeasurementUnitDataSource
import ke.co.xently.shopping.features.measurementunit.datasources.RemoteMeasurementUnitDataSource
import ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.MeasurementUnitService
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
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