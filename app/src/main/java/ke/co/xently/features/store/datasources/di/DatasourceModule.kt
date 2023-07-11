package ke.co.xently.features.store.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.store.datasources.LocalStoreDataSource
import ke.co.xently.features.store.datasources.RemoteStoreDataSource
import ke.co.xently.features.store.datasources.StoreDataSource
import ke.co.xently.features.store.datasources.remoteservices.StoreService
import ke.co.xently.features.store.models.Store
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