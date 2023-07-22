package ke.co.xently.shopping.features.attributes.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.attributes.datasources.remoteservices.AttributeAutoCompleteService

val LocalAttributeAutoCompleteService = staticCompositionLocalOf<AttributeAutoCompleteService> {
    AttributeAutoCompleteService.Fake
}