package ke.co.xently.shopping.features.attributesvalues.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService

val LocalAttributeValueAutoCompleteService =
    staticCompositionLocalOf<AttributeValueAutoCompleteService> {
        AttributeValueAutoCompleteService.Fake
    }