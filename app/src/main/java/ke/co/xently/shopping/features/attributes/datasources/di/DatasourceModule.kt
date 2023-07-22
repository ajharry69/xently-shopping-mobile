package ke.co.xently.shopping.features.attributes.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.attributes.datasources.AttributeDataSource
import ke.co.xently.shopping.features.attributes.datasources.LocalAttributeDataSource
import ke.co.xently.shopping.features.attributes.datasources.RemoteAttributeDataSource
import ke.co.xently.shopping.features.attributes.datasources.remoteservices.AttributeService
import ke.co.xently.shopping.features.attributes.models.Attribute
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): AttributeDataSource<Attribute.RemoteRequest, Attribute.RemoteResponse> {
        return RemoteAttributeDataSource(
            service = retrofit.create(AttributeService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): AttributeDataSource<Attribute.LocalEntityRequest, Attribute.LocalEntityResponse> {
        return LocalAttributeDataSource()
    }
}