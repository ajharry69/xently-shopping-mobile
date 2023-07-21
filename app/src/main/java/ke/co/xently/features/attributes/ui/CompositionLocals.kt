package ke.co.xently.features.attributes.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.attributes.datasources.remoteservices.AttributeAutoCompleteService

val LocalAttributeAutoCompleteService = staticCompositionLocalOf<AttributeAutoCompleteService> {
    AttributeAutoCompleteService.Fake
}