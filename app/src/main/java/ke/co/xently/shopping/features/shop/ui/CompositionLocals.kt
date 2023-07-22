package ke.co.xently.shopping.features.shop.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.shop.datasources.remoteservices.ShopAutoCompleteService

val LocalShopAutoCompleteService = staticCompositionLocalOf<ShopAutoCompleteService> {
    ShopAutoCompleteService.Fake
}