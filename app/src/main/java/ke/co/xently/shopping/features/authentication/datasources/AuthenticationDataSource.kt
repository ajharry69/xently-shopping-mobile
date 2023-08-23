package ke.co.xently.shopping.features.authentication.datasources

import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetResponse
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.models.ResetPasswordResponse
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.models.SignInResponse
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.models.SignUpResponse

interface AuthenticationDataSource {
    suspend fun signUp(request: SignUpRequest): SignUpResponse
    suspend fun saveSignUpResponse(response: SignUpResponse)
    suspend fun signIn(request: SignInRequest): SignInResponse
    suspend fun saveSignInResponse(response: SignInResponse)
    suspend fun requestPasswordReset(request: RequestPasswordResetRequest): RequestPasswordResetResponse
    suspend fun saveRequestPasswordResetResponse(response: RequestPasswordResetResponse)
    suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse
    suspend fun saveResetPasswordResponse(response: ResetPasswordResponse)
}