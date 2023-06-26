package ke.co.xently.products.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.LocalProductDataSource
import ke.co.xently.products.datasource.ProductDataSource
import ke.co.xently.products.datasource.RemoteProductDataSource
import ke.co.xently.products.datasource.remoteservices.ProductService
import ke.co.xently.products.models.Product
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