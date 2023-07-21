package ke.co.xently.features.products.ui

import androidx.compose.runtime.compositionLocalOf


val LocalAddProductStep = compositionLocalOf {
    AddProductStep.valueOfOrdinalOrFirstByOrdinal(0)
}
