package ke.co.xently.shopping.features.measurementunit.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class MeasurementUnitQuantityUIState(message: Int) : UIState(message) {
    sealed class StandaloneError(message: Int) : MeasurementUnitQuantityUIState(message) {
        object InvalidStandalone :
            StandaloneError(R.string.xently_button_label_invalid_standalone_unit_quantity)
    }

    sealed class LengthError(message: Int) : MeasurementUnitQuantityUIState(message) {
        object InvalidLength :
            LengthError(R.string.xently_button_label_invalid_length_unit_quantity)
    }

    sealed class WidthError(message: Int) : MeasurementUnitQuantityUIState(message) {
        object InvalidWidth : WidthError(R.string.xently_button_label_invalid_width_unit_quantity)
    }

    sealed class HeightError(message: Int) : MeasurementUnitQuantityUIState(message) {
        object InvalidHeight :
            HeightError(R.string.xently_button_label_invalid_height_unit_quantity)
    }

    object OK : MeasurementUnitQuantityUIState(android.R.string.ok)
}