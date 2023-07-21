package ke.co.xently.features.store.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.store.datasources.remoteservices.StoreAutoCompleteService

val LocalStoreAutoCompleteService = staticCompositionLocalOf<StoreAutoCompleteService> {
    StoreAutoCompleteService.Fake
}