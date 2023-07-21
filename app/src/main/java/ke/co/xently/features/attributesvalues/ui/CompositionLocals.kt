package ke.co.xently.features.attributesvalues.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService

val LocalAttributeValueAutoCompleteService =
    staticCompositionLocalOf<AttributeValueAutoCompleteService> {
        AttributeValueAutoCompleteService.Fake
    }