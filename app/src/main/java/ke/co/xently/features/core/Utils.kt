package ke.co.xently.features.core

import java.net.ConnectException

fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}

private val RETRYABLE_ERRORS = arrayOf(
    ConnectException::class,
)

val Throwable.isRetryable: Boolean
    get() = this::class in RETRYABLE_ERRORS