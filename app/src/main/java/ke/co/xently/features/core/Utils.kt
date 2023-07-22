package ke.co.xently.features.core


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import ke.co.xently.R
import kotlinx.coroutines.delay
import timber.log.Timber
import java.net.ConnectException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds


val Locale.javaLocale
    get() = java.util.Locale(language, region)


fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}

val CharSequence.hasEmojis: Boolean
    get() = any(Char::isSurrogate)

private val RETRYABLE_ERRORS = arrayOf(
    ConnectException::class,
)

val Throwable.isRetryable: Boolean
    get() = this::class in RETRYABLE_ERRORS

@Suppress("UnusedReceiverParameter")
val Context.numberFormat: NumberFormat
    get() = NumberFormat.getNumberInstance(Locale.current.javaLocale).apply {
        isGroupingUsed = true
    }

val Context.currencyNumberFormat: NumberFormat
    get() = NumberFormat.getCurrencyInstance(Locale.current.javaLocale).apply {
        currency = Currency.getInstance(getString(R.string.xently_iso_currency_code))
        isGroupingUsed = true
    }

inline fun Context.visitUriPage(
    uriString: String,
    logTag: String = "Utils",
    onActivityNotFound: () -> Unit = {},
) {
    val uri = Uri.parse(uriString)
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (ex: ActivityNotFoundException) {
        Timber.tag(logTag).e(ex, "An error was encountered when visiting: %s", uri)
        onActivityNotFound()
    }
}

fun LocalDateTime.toSystemDefaultZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return atZone(zoneId)
        .withZoneSameInstant(ZoneId.systemDefault())
}

@Composable
fun loadingIndicatorLabel(
    label: String,
    loading: Boolean,
    loadingLabelPrefix: String = label,
    loadingEvaluator: () -> Boolean = { true },
    vararg keys: Any?,
): String = if (!loading) {
    label
} else {
    var newLabel by remember {
        mutableStateOf(loadingLabelPrefix)
    }

    LaunchedEffect(loadingLabelPrefix, *keys) {
        var count = 0
        while (loadingEvaluator()) {
            if (count == 4) count = 0
            count += 1
            newLabel = buildString {
                append(loadingLabelPrefix)
                for (c in 1..count) {
                    append('.')
                }
            }
            delay(1.seconds)
        }
    }
    newLabel
}