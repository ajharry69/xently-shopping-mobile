package ke.co.xently.features.core.ui

import android.content.Context
import androidx.annotation.StringRes

internal open class UIState(@StringRes private val message: Int) {
    operator fun invoke(context: Context, vararg args: Any): String {
        return context.getString(message, *args)
    }
}