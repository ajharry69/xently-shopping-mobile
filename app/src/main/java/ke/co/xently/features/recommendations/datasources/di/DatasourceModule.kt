package ke.co.xently.features.recommendations.datasources.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.recommendations.datasources.RecommendationDataSource
import ke.co.xently.features.recommendations.datasources.RemoteRecommendationDataSource
import ke.co.xently.features.recommendations.datasources.remoteservices.RecommendationService
import ke.co.xently.features.recommendations.models.Recommendation
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofit: Retrofit): RecommendationDataSource<Recommendation.Request, Recommendation.Response> {
        return RemoteRecommendationDataSource(
            service = retrofit.create(RecommendationService::class.java),
        )
    }
}