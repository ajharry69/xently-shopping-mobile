package ke.co.xently.shopping.features.authentication.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.authentication.datasources.AuthenticationDataSource
import ke.co.xently.shopping.features.authentication.datasources.LocalAuthenticationDataSource
import ke.co.xently.shopping.features.authentication.datasources.RemoteAuthenticationDataSource
import ke.co.xently.shopping.features.authentication.datasources.remoteservices.AuthenticationService
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    @Named("remoteAuthenticationDataSource")
    fun provideRemoteDataSource(retrofit: Retrofit): AuthenticationDataSource {
        val service = retrofit.create(AuthenticationService::class.java)
        return RemoteAuthenticationDataSource(service = service)
    }

    @Provides
    @Singleton
    @Named("localAuthenticationDataSource")
    fun provideLocalDataSource(database: Database): AuthenticationDataSource {
        return LocalAuthenticationDataSource(database)
    }
}