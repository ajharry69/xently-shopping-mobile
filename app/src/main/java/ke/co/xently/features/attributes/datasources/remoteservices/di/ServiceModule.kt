package ke.co.xently.features.attributes.datasources.remoteservices.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.attributes.datasources.remoteservices.AttributeAutoCompleteService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAutoCompleteService(service: AttributeAutoCompleteService.Actual): AttributeAutoCompleteService
}