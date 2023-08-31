package ke.co.xently.shopping

import ke.co.xently.shopping.features.compareproducts.models.CompareProduct

sealed interface BottomSheet {
    object Ignore : BottomSheet
    data class CompareProductResponse(val data: CompareProduct.Response) : BottomSheet
}

val BottomSheet.open
    get() = when (this) {
        is BottomSheet.Ignore -> false
        is BottomSheet.CompareProductResponse -> data.comparisonList.isNotEmpty()
    }