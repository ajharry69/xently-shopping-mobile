package ke.co.xently.ui

import androidx.compose.ui.text.intl.Locale


val Locale.javaLocale
    get() = java.util.Locale(language, region)

