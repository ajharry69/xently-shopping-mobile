package ke.co.xently.shopping.features.products.ui.subscreens

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class ProductNameUIState(message: Int) : UIState(message) {
    object OK : ProductNameUIState(android.R.string.ok)

    object MissingProductName :
        ProductNameUIState(R.string.xently_button_label_missing_product_name)

    sealed class NamePluralError(message: Int) : ProductNameUIState(message) {
        object ImojisNotAllowed : NamePluralError(R.string.xently_error_imojis_not_allowed)
    }
}