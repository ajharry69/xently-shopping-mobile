package ke.co.xently.features.shop.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.shop.datasources.remoteservices.ShopAutoCompleteService

val LocalShopAutoCompleteService = staticCompositionLocalOf<ShopAutoCompleteService> {
    ShopAutoCompleteService.Fake
}