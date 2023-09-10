package ke.co.xently.shopping.datasource.remote

open class HttpException(
    val detail: Any? = null,
    val error: Any? = null,
    @Suppress("unused") val errorCode: String? = null,
    @Suppress("unused") var statusCode: Int? = null,
    @Suppress("unused") var unParsedErrorString: String? = null,
) : RuntimeException() {
    override fun toString(): String {
        return buildString {
            append(super.toString())
            append("=>")
            append(unParsedErrorString)
        }
    }

    // TODO: Override this class in every parent class...
    open fun hasFieldErrors(): Boolean {
        return false
    }

    override val message: String?
        get() = when (val errorMessage = detail ?: error) {
            null -> {
                super.message
            }

            is String -> {
                errorMessage
            }

            is List<*> -> {
                errorMessage.joinToString("\n")
            }

            else -> {
                throw IllegalStateException("'detail' or 'error' can only be a (nullable) String or List")
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        const val ERROR_CODE_TOKEN_EXPIRED = "expired_token"
        const val ERROR_CODE_INVALID_TOKEN = "invalid_token"
        const val ERROR_CODE_IO_ERROR = "io_error"
        const val ERROR_CODE_EMPTY_RESPONSE = "empty_response"

        fun Throwable.requiresAuthentication(): Boolean {
            return (this as? HttpException)?.run {
                (errorCode in listOf(
                    ERROR_CODE_TOKEN_EXPIRED,
                    ERROR_CODE_INVALID_TOKEN,
                )) || statusCode in listOf(401, 403)
            } ?: false
        }
    }
}