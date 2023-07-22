package ke.co.xently.shopping.features.brands.datasources.remoteservices.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.brands.datasources.remoteservices.BrandAutoCompleteService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAutoCompleteService(service: BrandAutoCompleteService.Actual): BrandAutoCompleteService
}