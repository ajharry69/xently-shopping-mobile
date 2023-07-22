package ke.co.xently.shopping.features.products.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.products.datasources.remoteservices.ProductAutoCompleteService


val LocalAddProductStep = compositionLocalOf {
    AddProductStep.valueOfOrdinalOrFirstByOrdinal(0)
}

val LocalProductAutoCompleteService = staticCompositionLocalOf<ProductAutoCompleteService> {
    ProductAutoCompleteService.Fake
}
