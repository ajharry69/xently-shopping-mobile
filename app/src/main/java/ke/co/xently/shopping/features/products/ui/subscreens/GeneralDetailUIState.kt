package ke.co.xently.shopping.features.products.ui.subscreens

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class GeneralDetailUIState(message: Int) : UIState(message) {
    sealed class UnitPriceError(message: Int) : GeneralDetailUIState(message)
    sealed class PackCountError(message: Int) : GeneralDetailUIState(message)

    object OK : GeneralDetailUIState(android.R.string.ok)

    object MissingUnitPrice : UnitPriceError(R.string.xently_button_label_missing_unit_price)

    object InvalidUnitPrice : UnitPriceError(R.string.xently_button_label_invalid_unit_price)

    object InvalidPackCount : PackCountError(R.string.xently_button_label_invalid_pack_count)
}