package ke.co.xently.shopping.features.store.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.store.datasources.remoteservices.StoreAutoCompleteService

val LocalStoreAutoCompleteService = staticCompositionLocalOf<StoreAutoCompleteService> {
    StoreAutoCompleteService.Fake
}