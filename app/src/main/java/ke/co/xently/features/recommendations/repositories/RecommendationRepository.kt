package ke.co.xently.features.recommendations.repositories

import ke.co.xently.features.recommendations.datasources.RecommendationDataSource
import ke.co.xently.features.recommendations.models.Recommendation
import javax.inject.Inject
import javax.inject.Singleton

sealed interface RecommendationRepository {
    suspend fun getRecommendations(request: Recommendation.Request): Result<List<Recommendation.Response>>

    object Fake : RecommendationRepository {
        override suspend fun getRecommendations(request: Recommendation.Request): Result<List<Recommendation.Response>> {
            return Result.success(emptyList())
        }
    }

    @Singleton
    class Actual @Inject constructor(
        private val remoteDataSource: RecommendationDataSource<Recommendation.Request, Recommendation.Response>,
    ) : RecommendationRepository {
        override suspend fun getRecommendations(request: Recommendation.Request) = try {
            remoteDataSource.getRecommendations(request).let {
                Result.success(it)
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}