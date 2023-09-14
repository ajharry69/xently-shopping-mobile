package ke.co.xently.shopping.features.authentication.datasources

import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetResponse
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.models.ResetPasswordResponse
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.models.SignInResponse
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.models.SignUpResponse
import ke.co.xently.shopping.features.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

abstract class AuthenticationDataSource {
    open fun getCurrentlySignedInUser(): Flow<User?> = emptyFlow()
    abstract suspend fun signUp(request: SignUpRequest): SignUpResponse
    open suspend fun saveSignUpResponse(response: SignUpResponse) {}

    abstract suspend fun signIn(request: SignInRequest): SignInResponse
    open suspend fun saveSignInResponse(response: SignInResponse) {}

    abstract suspend fun requestPasswordReset(request: RequestPasswordResetRequest): RequestPasswordResetResponse
    open suspend fun saveRequestPasswordResetResponse(response: RequestPasswordResetResponse) {}

    abstract suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse
    open suspend fun saveResetPasswordResponse(response: ResetPasswordResponse) {}
    open suspend fun deleteCurrentlySignedInUser() {}
}