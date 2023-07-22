package ke.co.xently.shopping.features.brands.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.brands.datasources.remoteservices.BrandAutoCompleteService

val LocalBrandAutoCompleteService = staticCompositionLocalOf<BrandAutoCompleteService> {
    BrandAutoCompleteService.Fake
}