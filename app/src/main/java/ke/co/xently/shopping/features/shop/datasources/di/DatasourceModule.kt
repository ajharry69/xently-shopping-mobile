package ke.co.xently.shopping.features.shop.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.shop.datasources.LocalShopDataSource
import ke.co.xently.shopping.features.shop.datasources.RemoteShopDataSource
import ke.co.xently.shopping.features.shop.datasources.ShopDataSource
import ke.co.xently.shopping.features.shop.datasources.remoteservices.ShopService
import ke.co.xently.shopping.features.shop.models.Shop
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