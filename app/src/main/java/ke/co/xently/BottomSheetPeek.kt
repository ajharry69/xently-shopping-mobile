package ke.co.xently

import ke.co.xently.features.compareproducts.models.CompareProduct
import ke.co.xently.features.recommendations.models.Recommendation

sealed interface BottomSheetPeek {
    object Ignore : BottomSheetPeek
    data class CompareProductResponse(val data: CompareProduct.Response) : BottomSheetPeek
    sealed interface RecommendationResponse : BottomSheetPeek {
        data class Single(val data: Recommendation.Response) : RecommendationResponse
        data class Many(val data: List<Recommendation.Response>) : RecommendationResponse
    }
}