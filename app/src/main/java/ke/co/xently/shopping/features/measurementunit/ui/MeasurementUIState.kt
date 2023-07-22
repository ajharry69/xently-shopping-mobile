package ke.co.xently.shopping.features.measurementunit.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class MeasurementUIState(message: Int) : UIState(message) {
    sealed class QuantityError(message: Int) : MeasurementUIState(message) {
        object InvalidQuantity : QuantityError(R.string.xently_button_label_invalid_unit_quantity)
    }

    sealed class NamePluralError(message: Int) : MeasurementUIState(message) {
        object ImojiNotAllowedError : NamePluralError(R.string.xently_error_imojis_not_allowed)
    }

    sealed class SymbolError(message: Int) : MeasurementUIState(message) {
        object ImojiNotAllowedError : SymbolError(R.string.xently_error_imojis_not_allowed)
    }

    sealed class SymbolPluralError(message: Int) : MeasurementUIState(message) {
        object ImojiNotAllowedError : SymbolPluralError(R.string.xently_error_imojis_not_allowed)
    }

    object OK : MeasurementUIState(android.R.string.ok)
}