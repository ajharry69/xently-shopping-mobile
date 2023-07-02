package ke.co.xently.features.products.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.products.datasources.LocalProductDataSource
import ke.co.xently.features.products.datasources.ProductDataSource
import ke.co.xently.features.products.datasources.RemoteProductDataSource
import ke.co.xently.features.products.datasources.remoteservices.ProductService
import ke.co.xently.features.products.models.Product
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): ProductDataSource<Product.RemoteRequest, Product.RemoteResponse> {
        return RemoteProductDataSource(
            service = retrofit.create(ProductService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse> {
        return LocalProductDataSource()
    }
}