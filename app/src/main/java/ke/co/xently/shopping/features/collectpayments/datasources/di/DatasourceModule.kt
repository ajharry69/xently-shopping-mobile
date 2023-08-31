package ke.co.xently.shopping.features.collectpayments.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.collectpayments.datasources.LocalMpesaPaymentDataSource
import ke.co.xently.shopping.features.collectpayments.datasources.MpesaPaymentDataSource
import ke.co.xently.shopping.features.collectpayments.datasources.RemoteMpesaPaymentDataSource
import ke.co.xently.shopping.features.collectpayments.datasources.remoteservices.MpesaPaymentService
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    @Named("remoteMpesaPaymentDataSource")
    fun provideRemoteDataSource(retrofit: Retrofit): MpesaPaymentDataSource {
        val service = retrofit.create(MpesaPaymentService::class.java)
        return RemoteMpesaPaymentDataSource(service = service)
    }

    @Provides
    @Singleton
    @Named("localMpesaPaymentDataSource")
    fun provideLocalDataSource(database: Database): MpesaPaymentDataSource {
        return LocalMpesaPaymentDataSource(database)
    }
}