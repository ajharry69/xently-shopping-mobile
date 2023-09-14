package ke.co.xently.shopping.features.authentication.datasources

import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetResponse
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.models.ResetPasswordResponse
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.models.SignInResponse
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.models.SignUpResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAuthenticationDataSource @Inject constructor(
    private val database: Database,
) : AuthenticationDataSource() {
    override fun getCurrentlySignedInUser() = database.userDao.get()

    override suspend fun signUp(request: SignUpRequest): SignUpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun saveSignUpResponse(response: SignUpResponse) {
        database.userDao.save(response.user)
    }

    override suspend fun signIn(request: SignInRequest): SignInResponse {
        TODO("Not yet implemented")
    }

    override suspend fun saveSignInResponse(response: SignInResponse) {
        database.userDao.save(response.user)
    }

    override suspend fun requestPasswordReset(request: RequestPasswordResetRequest): RequestPasswordResetResponse {
        TODO("Not yet implemented")
    }

    override suspend fun saveRequestPasswordResetResponse(response: RequestPasswordResetResponse) {
        database.userDao.save(response.user)
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        TODO("Not yet implemented")
    }

    override suspend fun saveResetPasswordResponse(response: ResetPasswordResponse) {
        database.userDao.save(response.user)
    }

    override suspend fun deleteCurrentlySignedInUser() {
        database.userDao.deleteCurrentlySignedInUser()
    }
}