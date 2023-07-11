package ke.co.xently.features.core.ui.autocomplete

import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class AutoCompleteTextFieldColors internal constructor(
    val containerColor: Color,
    val dividerColor: Color,
    val inputFieldColors: TextFieldColors,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AutoCompleteTextFieldColors

        if (containerColor != other.containerColor) return false
        if (dividerColor != other.dividerColor) return false
        if (inputFieldColors != other.inputFieldColors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + dividerColor.hashCode()
        result = 31 * result + inputFieldColors.hashCode()
        return result
    }
}