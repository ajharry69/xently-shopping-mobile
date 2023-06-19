package ke.co.xently.recommendations.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.xently.recommendations.datasource.RecommendationDataSource
import ke.co.xently.recommendations.datasource.RemoteRecommendationDataSource
import ke.co.xently.recommendations.models.Recommendation

@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModule {
    @Binds
    abstract fun bindRemoteDataSource(dataSource: RemoteRecommendationDataSource): RecommendationDataSource<Recommendation.Request, Recommendation.Response>
}