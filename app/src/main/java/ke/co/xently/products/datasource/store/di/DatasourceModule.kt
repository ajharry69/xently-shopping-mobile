package ke.co.xently.products.datasource.store.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.remoteservices.StoreService
import ke.co.xently.products.datasource.store.LocalStoreDataSource
import ke.co.xently.products.datasource.store.RemoteStoreDataSource
import ke.co.xently.products.datasource.store.StoreDataSource
import ke.co.xently.products.models.Store
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): StoreDataSource<Store.RemoteRequest, Store.RemoteResponse> {
        return RemoteStoreDataSource(
            service = retrofit.create(StoreService::class.java),
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(): StoreDataSource<Store.LocalEntityRequest, Store.LocalEntityResponse> {
        return LocalStoreDataSource()
    }
}