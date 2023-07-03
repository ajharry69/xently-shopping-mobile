package ke.co.xently.features.compareproducts.models

import android.icu.math.BigDecimal
import android.os.Parcelable
import ke.co.xently.features.store.models.Store
import kotlinx.parcelize.Parcelize

sealed interface CompareProduct {
    @Parcelize
    data class Request(val comparisonList: List<ComparisonListItem>) : CompareProduct, Parcelable {
        override fun hashCode(): Int {
            return comparisonList.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Request

            if (comparisonList != other.comparisonList) return false

            return true
        }

        @Parcelize
        data class ComparisonListItem(
            val name: String,
            val unitPrice: Number,
        ) : Parcelable {
            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + unitPrice.hashCode()
                return result
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ComparisonListItem

                if (name != other.name) return false
                if (unitPrice != other.unitPrice) return false

                return true
            }

            override fun toString(): String {
                return name
            }

            companion object {
                val default = ComparisonListItem(name = "", unitPrice = 0)
            }
        }

        companion object {
            val default = Request(comparisonList = emptyList())
        }
    }

    data class Response(
        val estimatedExpenditure: EstimatedExpenditure,
        val store: Store.LocalViewModel,
        val hit: Hit,
        val miss: Miss,
    ) : CompareProduct {
        data class EstimatedExpenditure(val unit: Number = 0, val total: Number = 0)
        data class Miss(val count: Int, val items: List<Item>) {
            @JvmInline
            value class Item(val value: String)
        }

        data class Hit(val count: Int, val items: List<Item>) {
            data class Item(
                val bestMatched: BestMatched,
                val shoppingList: ShoppingList,
            ) {
                data class ShoppingList(val name: String, val quantityToPurchase: Number = 1)
                data class BestMatched(
                    val name: String,
                    val unitPrice: BigDecimal,
                    /**
                     * price after multiplying the unit price by the quantity of items planned for purchase
                     */
                    val totalPrice: BigDecimal,
                    val pricePerBaseMeasurementUnitQuantity: BigDecimal,
                )
            }
        }
    }
}