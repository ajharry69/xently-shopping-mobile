package ke.co.xently.features.core


import android.content.Context
import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import ke.co.xently.R
import kotlinx.coroutines.delay
import java.net.ConnectException
import kotlin.time.Duration.Companion.seconds


val Locale.javaLocale
    get() = java.util.Locale(language, region)


fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}

private val RETRYABLE_ERRORS = arrayOf(
    ConnectException::class,
)

val Throwable.isRetryable: Boolean
    get() = this::class in RETRYABLE_ERRORS

val Context.currencyNumberFormat: NumberFormat
    get() = NumberFormat.getCurrencyInstance(Locale.current.javaLocale).apply {
        currency = Currency.getInstance(getString(R.string.xently_iso_currency_code))
        isGroupingUsed = true
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