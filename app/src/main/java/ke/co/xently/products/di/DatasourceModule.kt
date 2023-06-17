package ke.co.xently.products.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.products.datasource.LocalProductDataSource
import ke.co.xently.products.datasource.ProductDataSource
import ke.co.xently.products.datasource.RemoteProductDataSource
import ke.co.xently.products.models.Product

@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModule {
    @Binds
    abstract fun bindRemoteDataSource(dataSource: RemoteProductDataSource): ProductDataSource<Product.RemoteRequest, Product.RemoteResponse>

    @Binds
    abstract fun bindLocalDataSource(dataSource: LocalProductDataSource): ProductDataSource<Product.LocalEntityRequest, Product.LocalEntityResponse>
}