package ke.co.xently.shopping.features.compareproducts.repositories.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.features.compareproducts.repositories.CompareProductRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(repository: CompareProductRepository.Actual): CompareProductRepository
}