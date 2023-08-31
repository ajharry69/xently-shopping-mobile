package ke.co.xently.shopping.features.authentication.repositories

import ke.co.xently.shopping.features.authentication.datasources.AuthenticationDataSource
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed interface AuthenticationRepository {
    fun getCurrentlySignedInUser(): Flow<User?>
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun signIn(request: SignInRequest): Result<Unit>
    suspend fun requestPasswordReset(request: RequestPasswordResetRequest): Result<Unit>
    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit>

    object Fake : AuthenticationRepository {
        override fun getCurrentlySignedInUser() = emptyFlow<User>()

        override suspend fun signUp(request: SignUpRequest): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun signIn(request: SignInRequest): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun requestPasswordReset(request: RequestPasswordResetRequest): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Singleton
    class Actual @Inject constructor(
        @Named("localAuthenticationDataSource")
        private val localDataSource: AuthenticationDataSource,
        @Named("remoteAuthenticationDataSource")
        private val remoteDataSource: AuthenticationDataSource,
    ) : AuthenticationRepository {
        override fun getCurrentlySignedInUser() = localDataSource.getCurrentlySignedInUser()

        override suspend fun signUp(request: SignUpRequest): Result<Unit> {
            return try {
                remoteDataSource.signUp(request).let {
                    localDataSource.saveSignUpResponse(it)
                }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }

        override suspend fun signIn(request: SignInRequest): Result<Unit> {
            return try {
                remoteDataSource.signIn(request).let {
                    localDataSource.saveSignInResponse(it)
                }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }

        override suspend fun requestPasswordReset(request: RequestPasswordResetRequest): Result<Unit> {
            return try {
                remoteDataSource.requestPasswordReset(request).let {
                    localDataSource.saveRequestPasswordResetResponse(it)
                }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }

        override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
            return try {
                remoteDataSource.resetPassword(request).let {
                    localDataSource.saveResetPasswordResponse(it)
                }.let {
                    Result.success(it)
                }
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}