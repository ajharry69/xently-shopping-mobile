package ke.co.xently.features.recommendations.models

import android.icu.math.BigDecimal
import android.location.Location
import android.os.Parcelable
import ke.co.xently.features.store.models.Store
import kotlinx.parcelize.Parcelize

sealed interface Recommendation {
    @Parcelize
    data class Request(
        val currentLocation: Location,
        val storeDistanceMeters: Number,
        val shoppingList: List<ShoppingListItem>,
    ) : Recommendation, Parcelable {
        override fun hashCode(): Int {
            var result = currentLocation.hashCode()
            result = 31 * result + storeDistanceMeters.hashCode()
            result = 31 * result + shoppingList.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Request

            if (currentLocation != other.currentLocation) return false
            if (storeDistanceMeters != other.storeDistanceMeters) return false
            if (shoppingList != other.shoppingList) return false

            return true
        }

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
            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + quantityToPurchase.hashCode()
                result = 31 * result + enforceStrictMeasurementUnit.hashCode()
                return result
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ShoppingListItem

                if (name != other.name) return false
                if (quantityToPurchase != other.quantityToPurchase) return false
                if (enforceStrictMeasurementUnit != other.enforceStrictMeasurementUnit) return false

                return true
            }

            override fun toString(): String {
                return name
            }

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
        val store: Store.LocalViewModel,
        val hit: Hit,
        val miss: Miss,
    ) : Recommendation {
        data class EstimatedExpenditure(val unit: Number = 0, val total: Number = 0) {
            companion object {
                val default = EstimatedExpenditure()
            }
        }

        data class Miss(val count: Int, val items: List<Item>) {
            @JvmInline
            value class Item(val value: String) {
                companion object {
                    val default = Item(value = "")
                }
            }

            companion object {
                val default = Miss(
                    count = 0,
                    items = emptyList(),
                )
            }
        }

        data class Hit(val count: Int, val items: List<Item>) {
            data class Item(
                val bestMatched: BestMatched,
                val shoppingList: ShoppingList,
            ) {
                data class ShoppingList(val name: String, val quantityToPurchase: Number = 1) {
                    companion object {
                        val default = ShoppingList(name = "")
                    }
                }

                data class BestMatched(
                    val name: String,
                    val unitPrice: BigDecimal,
                    /**
                     * price after multiplying the unit price by the quantity of items planned for purchase
                     */
                    val totalPrice: BigDecimal,
                    val pricePerBaseMeasurementUnitQuantity: BigDecimal,
                ) {
                    companion object {
                        val default = BestMatched(
                            name = "",
                            unitPrice = BigDecimal.ZERO,
                            totalPrice = BigDecimal.ZERO,
                            pricePerBaseMeasurementUnitQuantity = BigDecimal.ZERO,
                        )
                    }
                }

                companion object {
                    val default = Item(
                        bestMatched = BestMatched.default,
                        shoppingList = ShoppingList.default,
                    )
                }
            }

            companion object {
                val default = Hit(
                    count = 0,
                    items = emptyList(),
                )
            }
        }

        companion object {
            val default = Response(
                estimatedExpenditure = EstimatedExpenditure.default,
                store = Store.LocalViewModel.default,
                hit = Hit.default,
                miss = Miss.default,
            )
        }
    }
}