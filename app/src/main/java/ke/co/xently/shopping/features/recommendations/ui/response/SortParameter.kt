package ke.co.xently.shopping.features.recommendations.ui.response

import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortBy
import kotlinx.serialization.Serializable

@Serializable
data class SortParameter(
    val orderBy: OrderBy,
    val sortBy: RecommendationResponseSortBy,
) {
    companion object {
        val default = SortParameter(
            orderBy = OrderBy.Ascending,
            sortBy = RecommendationResponseSortBy.Default,
        )
    }
}