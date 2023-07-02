package ke.co.xently.features.recommendations.datasources.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.features.recommendations.datasources.RecommendationDataSource
import ke.co.xently.features.recommendations.datasources.RemoteRecommendationDataSource
import ke.co.xently.features.recommendations.models.Recommendation

@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModule {
    @Binds
    abstract fun bindRemoteDataSource(dataSource: RemoteRecommendationDataSource): RecommendationDataSource<Recommendation.Request, Recommendation.Response>
}