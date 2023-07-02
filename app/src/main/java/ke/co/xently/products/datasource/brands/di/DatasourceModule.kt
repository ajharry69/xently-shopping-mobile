package ke.co.xently.products.datasource.brands.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.brands.BrandDataSource
import ke.co.xently.products.datasource.brands.LocalBrandDataSource
import ke.co.xently.products.datasource.brands.RemoteBrandDataSource
import ke.co.xently.products.datasource.remoteservices.BrandService
import ke.co.xently.products.models.Brand
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): BrandDataSource<Brand.RemoteRequest, Brand.RemoteResponse> {
        return RemoteBrandDataSource(
            service = retrofit.create(BrandService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): BrandDataSource<Brand.LocalEntityRequest, Brand.LocalEntityResponse> {
        return LocalBrandDataSource()
    }
}