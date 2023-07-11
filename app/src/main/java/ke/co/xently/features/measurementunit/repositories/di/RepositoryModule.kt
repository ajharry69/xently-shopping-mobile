package ke.co.xently.features.measurementunit.repositories.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.measurementunit.repositories.MeasurementUnitRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(repository: MeasurementUnitRepository.Actual): MeasurementUnitRepository
}