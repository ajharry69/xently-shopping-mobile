package ke.co.xently.shopping.features.compareproducts.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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