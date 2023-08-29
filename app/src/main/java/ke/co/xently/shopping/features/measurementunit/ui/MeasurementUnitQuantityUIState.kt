package ke.co.xently.shopping.features.measurementunit.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class MeasurementUnitQuantityUIState(message: Int) : UIState(message) {
    sealed class QuantityError(message: Int) : MeasurementUnitQuantityUIState(message) {
        object InvalidQuantity : QuantityError(R.string.xently_button_label_invalid_unit_quantity)
    }

    object OK : MeasurementUnitQuantityUIState(android.R.string.ok)
}