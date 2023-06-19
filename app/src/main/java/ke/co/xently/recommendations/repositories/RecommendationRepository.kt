package ke.co.xently.recommendations.repositories

import ke.co.xently.recommendations.datasource.RecommendationDataSource
import ke.co.xently.recommendations.models.Recommendation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val remoteDataSource: RecommendationDataSource<Recommendation.Request, Recommendation.Response>,
) {
    suspend fun getRecommendations(request: Recommendation.Request): Result<List<Recommendation.Response>> {
        return remoteDataSource.getRecommendations(request).let {
            Result.success(it)
        }
    }
}