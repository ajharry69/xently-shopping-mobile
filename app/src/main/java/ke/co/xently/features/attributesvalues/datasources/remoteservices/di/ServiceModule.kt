package ke.co.xently.features.attributesvalues.datasources.remoteservices.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAutoCompleteService(service: AttributeValueAutoCompleteService.Actual): AttributeValueAutoCompleteService
}