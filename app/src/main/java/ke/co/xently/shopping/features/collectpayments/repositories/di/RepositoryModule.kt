package ke.co.xently.shopping.features.collectpayments.repositories.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.collectpayments.repositories.MpesaPaymentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(repository: MpesaPaymentRepository.Actual): MpesaPaymentRepository
}