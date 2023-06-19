package ke.co.xently.recommendations.models

import android.icu.math.BigDecimal
import android.location.Location
import android.os.Parcelable
import ke.co.xently.products.models.Store
import kotlinx.parcelize.Parcelize

sealed interface Recommendation {
    @Parcelize
    data class Request(
        val currentLocation: Location,
        val storeDistanceMeters: Number,
        val shoppingList: List<ShoppingListItem>,
    ) : Recommendation, Parcelable {
        @Parcelize
        data class ShoppingListItem(
            val name: String,
            val quantityToPurchase: Number = 1,
            /**
             * If the customer flags strictness on the measurement unit, don't strip out the
             * measurement unit otherwise, strip out the measurement unit and then pick up
             * the cheapest and ensure the measurement unit quantity adds up to the original.
             * For example, 2, 400-gram bread worth 45/= should be recommended if the shopping
             * list had 800-gram bread instead of 100/=, 800-gram bread.
             *
             * When we reach a point where we care about the environment, we should recommend
             * the 800g bread if both breads are wrapped in a plastic bag.
             */
            val enforceStrictMeasurementUnit: Boolean = true,
        ) : Parcelable {
            companion object {
                val default = ShoppingListItem(name = "")
            }
        }

        companion object {
            val default = Request(
                currentLocation = Location(null),
                storeDistanceMeters = 50,
                shoppingList = emptyList(),
            )
        }
    }

    data class Response(
        val estimatedExpenditure: EstimatedExpenditure,
        val store: Store.LocalEntityResponse,
        val hit: Hit,
        val miss: Miss,
    ) : Recommendation {
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