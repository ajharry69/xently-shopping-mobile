package ke.co.xently.shopping.features.authentication.datasources

import ke.co.xently.shopping.features.authentication.datasources.remoteservices.AuthenticationService
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetRequest
import ke.co.xently.shopping.features.authentication.models.RequestPasswordResetResponse
import ke.co.xently.shopping.features.authentication.models.ResetPasswordRequest
import ke.co.xently.shopping.features.authentication.models.ResetPasswordResponse
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.models.SignInResponse
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.models.SignUpResponse
import ke.co.xently.shopping.remotedatasource.SendHttpRequest
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAuthenticationDataSource @Inject constructor(
    private val service: AuthenticationService,
) : AuthenticationDataSource() {
    override suspend fun signUp(request: SignUpRequest): SignUpResponse {
        return SendHttpRequest {
            service.signUp(request)
        }.getOrThrow()
    }

    override suspend fun signIn(request: SignInRequest): SignInResponse {
        return SendHttpRequest {
            val usernameAndPassword = "${request.email}:${request.password}"
            val base64EncodedCredentials =
                Base64.getEncoder().encodeToString(usernameAndPassword.toByteArray())
            service.signIn("Basic $base64EncodedCredentials")
        }.getOrThrow()
    }

    override suspend fun requestPasswordReset(request: RequestPasswordResetRequest): RequestPasswordResetResponse {
        return SendHttpRequest {
            service.requestPasswordReset(request)
        }.getOrThrow()
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        return SendHttpRequest {
            service.resetPassword(request)
        }.getOrThrow()
    }
}