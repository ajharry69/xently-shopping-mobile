package ke.co.xently.features.core.ui.autocomplete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.R
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.ConfigurationCompat
import java.util.Locale


@Immutable
@JvmInline
internal value class Strings private constructor(
    @Suppress("unused") private val value: Int = nextId()
) {
    companion object {
        private var id = 0
        private fun nextId() = id++

        val NavigationMenu = Strings()
        val CloseDrawer = Strings()
        val CloseSheet = Strings()
        val DefaultErrorMessage = Strings()
        val ExposedDropdownMenu = Strings()
        val SliderRangeStart = Strings()
        val SliderRangeEnd = Strings()
        val SearchBarSearch = Strings()
        val SuggestionsAvailable = Strings()
    }
}

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings): String {
    LocalConfiguration.current
    val resources = LocalContext.current.resources
    return when (string) {
        Strings.NavigationMenu -> resources.getString(R.string.navigation_menu)
        Strings.CloseDrawer -> resources.getString(R.string.close_drawer)
        Strings.CloseSheet -> resources.getString(R.string.close_sheet)
        Strings.DefaultErrorMessage -> resources.getString(R.string.default_error_message)
        Strings.ExposedDropdownMenu -> resources.getString(R.string.dropdown_menu)
        Strings.SliderRangeStart -> resources.getString(R.string.range_start)
        Strings.SliderRangeEnd -> resources.getString(R.string.range_end)
        else -> ""
    }
}

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings, vararg formatArgs: Any): String {
    val raw = getString(string)
    val locale =
        ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
    return String.format(locale, raw, *formatArgs)
}
