package ke.co.xently.shopping.features.compareproducts.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class CompareProductRequestUIState(message: Int) : UIState(message) {
    sealed class UnitPriceError(message: Int) : CompareProductRequestUIState(message)
    sealed class NameError(message: Int) : CompareProductRequestUIState(message)

    object NameAndUnitPriceBlank :
        CompareProductRequestUIState(R.string.xently_error_blank_not_allowed)

    object OK : CompareProductRequestUIState(android.R.string.ok)

    object MissingUnitPrice : UnitPriceError(R.string.xently_error_missing_unit_price)

    object InvalidUnitPrice : UnitPriceError(R.string.xently_error_invalid_unit_price)

    object MissingName : NameError(R.string.xently_error_missing_name)
}