package ke.co.xently.shopping.features.measurementunit.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class MeasurementUnitNameUIState(message: Int) : UIState(message) {
    sealed class NamePluralError(message: Int) : MeasurementUnitNameUIState(message) {
        object ImojiNotAllowedError : NamePluralError(R.string.xently_error_imojis_not_allowed)
    }

    sealed class SymbolError(message: Int) : MeasurementUnitNameUIState(message) {
        object ImojiNotAllowedError : SymbolError(R.string.xently_error_imojis_not_allowed)
    }

    sealed class SymbolPluralError(message: Int) : MeasurementUnitNameUIState(message) {
        object ImojiNotAllowedError : SymbolPluralError(R.string.xently_error_imojis_not_allowed)
    }

    object OK : MeasurementUnitNameUIState(android.R.string.ok)
}