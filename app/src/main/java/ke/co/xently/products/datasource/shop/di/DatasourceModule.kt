package ke.co.xently.products.datasource.shop.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.remoteservices.ShopService
import ke.co.xently.products.datasource.shop.LocalShopDataSource
import ke.co.xently.products.datasource.shop.RemoteShopDataSource
import ke.co.xently.products.datasource.shop.ShopDataSource
import ke.co.xently.products.models.Shop
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): ShopDataSource<Shop.RemoteRequest, Shop.RemoteResponse> {
        return RemoteShopDataSource(
            service = retrofit.create(ShopService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): ShopDataSource<Shop.LocalEntityRequest, Shop.LocalEntityResponse> {
        return LocalShopDataSource()
    }
}