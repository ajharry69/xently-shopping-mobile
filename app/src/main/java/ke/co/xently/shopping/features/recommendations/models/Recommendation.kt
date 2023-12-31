package ke.co.xently.shopping.features.recommendations.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import ke.co.xently.shopping.features.core.models.BigDecimalSerializer
import ke.co.xently.shopping.features.core.models.Location
import ke.co.xently.shopping.features.store.models.Store
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

sealed interface Recommendation {
    @Keep
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

        @Keep
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
                currentLocation = Location.default,
                storeDistanceMeters = 50000,
                shoppingList = emptyList(),
            )
        }
    }

    @Keep
    @Serializable
    data class Response(
        val estimatedExpenditure: EstimatedExpenditure,
        @SerializedName("redactedStore")
        val store: Store.LocalViewModel,
        val encryptedStoreJson: String,
        val hit: Hit,
        val miss: Miss,
    ) : Recommendation {
        fun hasAnOnlineStore() = store.hasAnOnlineStore()

        @Keep
        @Serializable
        data class EstimatedExpenditure(val unit: Double = 0.0, val total: Double = 0.0) {
            companion object {
                val default = EstimatedExpenditure()
            }
        }

        @Keep
        @Serializable
        data class Miss(val count: Int, val items: List<Item> = emptyList()) {
            @JvmInline
            @Serializable
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

        @Keep
        @Serializable
        data class Hit(val count: Int, val items: List<Item> = emptyList()) {
            @Keep
            @Serializable
            data class Item(
                val bestMatched: BestMatched,
                val shoppingList: ShoppingList,
            ) {
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as Item

                    if (bestMatched != other.bestMatched) return false
                    if (shoppingList != other.shoppingList) return false

                    return true
                }

                override fun hashCode(): Int {
                    var result = bestMatched.hashCode()
                    result = 31 * result + shoppingList.hashCode()
                    return result
                }

                @Keep
                @Serializable
                data class ShoppingList(val name: String, val quantityToPurchase: Int = 1) {
                    override fun hashCode(): Int {
                        return name.hashCode()
                    }

                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (javaClass != other?.javaClass) return false

                        other as ShoppingList

                        if (name != other.name) return false

                        return true
                    }

                    companion object {
                        val default = ShoppingList(name = "")
                    }
                }

                @Keep
                @Serializable
                data class BestMatched(
                    val name: String,
                    @Serializable(with = BigDecimalSerializer::class)
                    val unitPrice: BigDecimal,
                    /**
                     * price after multiplying the unit price by the quantity of items planned for purchase
                     */
                    @Serializable(with = BigDecimalSerializer::class)
                    val totalPrice: BigDecimal,
                    /**
                     * Expected in the format: 2023-07-10T20:37:00(TZ OFFSET)
                     */
                    @SerializedName("latestDateOfPurchase")
                    val latestDateOfPurchaseZonedDateTimeString: String,
                ) {
                    override fun hashCode(): Int {
                        return name.hashCode()
                    }

                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (javaClass != other?.javaClass) return false

                        other as BestMatched

                        if (name != other.name) return false

                        return true
                    }

                    companion object {
                        val LATEST_DATE_OF_PURCHASE_FORMAT: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                        val default = BestMatched(
                            name = "",
                            unitPrice = BigDecimal.ZERO,
                            totalPrice = BigDecimal.ZERO,
                            latestDateOfPurchaseZonedDateTimeString = OffsetDateTime.now()
                                .format(LATEST_DATE_OF_PURCHASE_FORMAT),
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
                encryptedStoreJson = "",
                hit = Hit.default,
                miss = Miss.default,
            )
        }
    }
}