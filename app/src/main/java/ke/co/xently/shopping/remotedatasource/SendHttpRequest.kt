package ke.co.xently.shopping.remotedatasource

import retrofit2.Response
import kotlin.reflect.KClass

object SendHttpRequest {
    @Suppress("UNCHECKED_CAST")
    suspend operator fun <T, E : HttpException> invoke(
        errorClass: KClass<E>,
        request: suspend () -> Response<T>,
    ): Result<T> {
        val response = request.invoke() // Initiate actual network request call
        val (statusCode, body, errorBody) = Triple(
            response.code(),
            response.body(),
            response.errorBody()
        )
        return if (response.isSuccessful) {
            if (statusCode == 204) {
                throw HttpException(
                    "No results",
                    errorCode = HttpException.ERROR_CODE_EMPTY_RESPONSE,
                    statusCode = 204
                )
            } else {
                Result.success(body ?: Any() as T)
            }
        } else {
            throw try {
                Serialization.JSON_CONVERTER.fromJson(
                    // The following is potentially blocking! Assume the consumer will call the
                    // suspend function from IO dispatcher.
                    errorBody!!.string(),
                    errorClass.java,
                )
            } catch (ex: IllegalStateException) {
                HttpException(response.message(), statusCode = statusCode)
            }.apply {
                if (this.statusCode == null) {
                    this.statusCode = statusCode
                }
            }
        }
    }

    suspend operator fun <T> invoke(request: suspend () -> Response<T>) =
        invoke(errorClass = HttpException::class, request = request)
}