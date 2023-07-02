package ke.co.xently.features.brands.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.brands.datasources.BrandDataSource
import ke.co.xently.features.brands.datasources.LocalBrandDataSource
import ke.co.xently.features.brands.datasources.RemoteBrandDataSource
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.features.products.datasources.remoteservices.BrandService
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