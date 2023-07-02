package ke.co.xently.products.datasource.attributesvalues.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.attributesvalues.AttributeValueDataSource
import ke.co.xently.products.datasource.attributesvalues.LocalAttributeValueDataSource
import ke.co.xently.products.datasource.attributesvalues.RemoteAttributeValueDataSource
import ke.co.xently.products.datasource.remoteservices.AttributeValueService
import ke.co.xently.products.models.AttributeValue
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