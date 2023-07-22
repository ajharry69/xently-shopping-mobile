package ke.co.xently.shopping

import ke.co.xently.shopping.features.compareproducts.models.CompareProduct
import ke.co.xently.shopping.features.recommendations.models.Recommendation

sealed interface BottomSheet {
    object Ignore : BottomSheet
    data class CompareProductResponse(val data: CompareProduct.Response) : BottomSheet
    sealed interface RecommendationResponse : BottomSheet {
        data class Single(val data: Recommendation.Response) : RecommendationResponse
        data class Many(val data: List<Recommendation.Response>) : RecommendationResponse
    }
}

val BottomSheet.open
    get() = when (this) {
        is BottomSheet.Ignore -> false
        is BottomSheet.CompareProductResponse -> data.comparisonList.isNotEmpty()
        is BottomSheet.RecommendationResponse.Single -> true
        is BottomSheet.RecommendationResponse.Many -> data.isNotEmpty()
    }