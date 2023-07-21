package ke.co.xently.features.brands.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.brands.datasources.remoteservices.BrandAutoCompleteService

val LocalBrandAutoCompleteService = staticCompositionLocalOf<BrandAutoCompleteService> {
    BrandAutoCompleteService.Fake
}