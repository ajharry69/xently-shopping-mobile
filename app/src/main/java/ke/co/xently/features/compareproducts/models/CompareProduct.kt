package ke.co.xently.features.compareproducts.models

import android.os.Parcelable
import ke.co.xently.features.core.OrderBy
import kotlinx.parcelize.Parcelize

sealed interface CompareProduct {
    val comparisonList: List<ComparisonListItem>

    @Parcelize
    data class Request(
        override val comparisonList: List<ComparisonListItem>,
        val orderBy: OrderBy,
    ) : CompareProduct, Parcelable {
        override fun hashCode(): Int {
            var result = comparisonList.hashCode()
            result = 31 * result + orderBy.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Request

            if (comparisonList != other.comparisonList) return false
            if (orderBy != other.orderBy) return false

            return true
        }

        companion object {
            val default = Request(comparisonList = emptyList(), orderBy = OrderBy.Ascending)
        }
    }

    data class Response(override val comparisonList: List<ComparisonListItem>) : CompareProduct
}