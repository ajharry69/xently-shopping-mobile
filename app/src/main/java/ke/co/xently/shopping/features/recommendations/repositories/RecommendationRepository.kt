package ke.co.xently.shopping.features.recommendations.repositories

import ke.co.xently.shopping.features.recommendations.datasources.RecommendationDataSource
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import ke.co.xently.shopping.features.recommendations.models.toLocalCache
import ke.co.xently.shopping.features.recommendations.models.toViewModel
import ke.co.xently.shopping.features.security.DataEncryption
import ke.co.xently.shopping.features.store.models.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed interface RecommendationRepository {
    fun getLatestRecommendations(): Flow<RecommendationResponse.ViewModel?>
    suspend fun requestRecommendations(request: Recommendation.Request): Result<Unit>
    suspend fun getDecryptionCredentials(): Result<Unit>

    object Fake : RecommendationRepository {
        override fun getLatestRecommendations(): Flow<RecommendationResponse.ViewModel?> {
            return emptyFlow()
        }

        override suspend fun requestRecommendations(request: Recommendation.Request): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun getDecryptionCredentials(): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Singleton
    class Actual @Inject constructor(
        @Named("remoteRecommendationDataSource")
        private val remoteDataSource: RecommendationDataSource,
        @Named("localRecommendationDataSource")
        private val localDataSource: RecommendationDataSource,
    ) : RecommendationRepository {
        @OptIn(ExperimentalSerializationApi::class)
        override fun getLatestRecommendations(): Flow<RecommendationResponse.ViewModel?> {
            return localDataSource.getLatestRecommendations().mapLatest { recommendationResponse ->
                val decryptionCredentials =
                    recommendationResponse?.toLocalCache()?.decryptionCredentials

                recommendationResponse?.toViewModel()?.run {
                    if (decryptionCredentials == null) {
                        this
                    } else {
                        val base64EncodedIvParameterSpec =
                            decryptionCredentials.base64EncodedIVParameterSpec
                        val secretKeyPassword = decryptionCredentials.secretKeyPassword
                        val secretKeySalt = "${secretKeyPassword}:${base64EncodedIvParameterSpec}"
                        val recommendations = recommendations.map {
                            val decryptedStoreJson = DataEncryption.decrypt(
                                cipherText = it.encryptedStoreJson,
                                key = DataEncryption.getKeyFromPassword(
                                    password = secretKeyPassword,
                                    salt = secretKeySalt,
                                ),
                                iv = DataEncryption.generateIv(base64EncodedIvParameterSpec),
                            )
                            val json = Json {
                                ignoreUnknownKeys = true
                            }
                            val store =
                                json.decodeFromString<Store.LocalViewModel>(decryptedStoreJson)
                            it.copy(store = store)
                        }
                        copy(recommendations = recommendations)
                    }
                }
            }
        }

        override suspend fun requestRecommendations(request: Recommendation.Request) = try {
            val response = remoteDataSource.getRecommendations(request)
            localDataSource.saveRecommendationResponse(response)
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }

        override suspend fun getDecryptionCredentials(): Result<Unit> {
            return try {
                val requestId = localDataSource.getLatestUnprocessedRecommendationRequestId()
                remoteDataSource.getDecryptionCredentials(requestId).let {
                    localDataSource.saveDecryptionCredentials(requestId, it)
                }
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}