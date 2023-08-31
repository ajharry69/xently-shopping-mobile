package ke.co.xently.shopping.features.shop.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class ShopUIState(message: Int) : UIState(message) {
    object OK : ShopUIState(android.R.string.ok)

    object MissingShopName : ShopUIState(R.string.xently_error_missing_shop_name)
}