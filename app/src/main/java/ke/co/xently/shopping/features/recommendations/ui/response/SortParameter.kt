package ke.co.xently.shopping.features.recommendations.ui.response

import android.os.Parcelable
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortBy
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortParameter(
    val orderBy: OrderBy,
    val sortBy: RecommendationResponseSortBy,
) : Parcelable {
    companion object {
        val default = SortParameter(
            orderBy = OrderBy.Ascending,
            sortBy = RecommendationResponseSortBy.Default,
        )
    }
}