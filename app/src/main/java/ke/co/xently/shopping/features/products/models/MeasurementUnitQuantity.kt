package ke.co.xently.shopping.features.products.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MeasurementUnitQuantity(
    val standalone: Float,
    val threeDimension: ThreeDimension?,
) : Parcelable {
    override fun toString(): String {
        if (threeDimension == null) {
            return standalone.toString().replace(".0", "")
        }
        val length = threeDimension.length.toString().replace(".0", "")
        val width = threeDimension.width.toString().replace(".0", "")
        val height = threeDimension.height.toString().replace(".0", "")
        return "${length}x${width}x${height}"
    }

    @Serializable
    @Parcelize
    data class ThreeDimension(
        val length: Float,
        val width: Float,
        val height: Float,
    ) : Parcelable {
        companion object {
            val default = ThreeDimension(
                length = 1f,
                width = 1f,
                height = 1f,
            )
        }
    }

    companion object {
        val default: MeasurementUnitQuantity = MeasurementUnitQuantity(
            standalone = 1f,
            threeDimension = null,
        )
    }
}