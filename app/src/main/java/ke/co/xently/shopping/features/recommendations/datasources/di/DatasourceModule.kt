package ke.co.xently.shopping.features.recommendations.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.recommendations.datasources.LocalRecommendationDataSource
import ke.co.xently.shopping.features.recommendations.datasources.RecommendationDataSource
import ke.co.xently.shopping.features.recommendations.datasources.RemoteRecommendationDataSource
import ke.co.xently.shopping.features.recommendations.datasources.remoteservices.RecommendationService
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    @Named("remoteRecommendationDataSource")
    fun provideRemoteDataSource(retrofit: Retrofit): RecommendationDataSource {
        return RemoteRecommendationDataSource(
            service = retrofit.create(RecommendationService::class.java),
        )
    }

    @Provides
    @Singleton
    @Named("localRecommendationDataSource")
    fun provideLocalDataSource(database: Database): RecommendationDataSource {
        return LocalRecommendationDataSource(database)
    }
}