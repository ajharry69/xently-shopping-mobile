package ke.co.xently.features.compareproducts.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.compareproducts.datasources.CompareProductDataSource
import ke.co.xently.features.compareproducts.datasources.RemoteCompareProductDataSource
import ke.co.xently.features.compareproducts.datasources.remoteservices.CompareProductService
import ke.co.xently.features.compareproducts.models.CompareProduct
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): CompareProductDataSource<CompareProduct.Request, CompareProduct.Response> {
        return RemoteCompareProductDataSource(
            service = retrofit.create(CompareProductService::class.java),
        )
    }
}