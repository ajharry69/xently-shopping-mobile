package ke.co.xently.products.datasource.attributes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.attributes.AttributeDataSource
import ke.co.xently.products.datasource.attributes.LocalAttributeDataSource
import ke.co.xently.products.datasource.attributes.RemoteAttributeDataSource
import ke.co.xently.products.datasource.remoteservices.AttributeService
import ke.co.xently.products.models.Attribute
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