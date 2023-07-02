package ke.co.xently.features.attributesvalues.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.attributesvalues.datasources.AttributeValueDataSource
import ke.co.xently.features.attributesvalues.datasources.LocalAttributeValueDataSource
import ke.co.xently.features.attributesvalues.datasources.RemoteAttributeValueDataSource
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.products.datasources.remoteservices.AttributeValueService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): AttributeValueDataSource<AttributeValue.RemoteRequest, AttributeValue.RemoteResponse> {
        return RemoteAttributeValueDataSource(
            service = retrofit.create(AttributeValueService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): AttributeValueDataSource<AttributeValue.LocalEntityRequest, AttributeValue.LocalEntityResponse> {
        return LocalAttributeValueDataSource()
    }
}