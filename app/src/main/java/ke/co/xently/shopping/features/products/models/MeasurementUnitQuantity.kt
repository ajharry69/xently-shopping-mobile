package ke.co.xently.shopping.features.products.models

import android.os.Parcelable
import ke.co.xently.shopping.features.core.toStringWithoutUnnecessaryDigitsAfterDecimalPoint
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MeasurementUnitQuantity(
    val standalone: Float,
    val twoOrThreeDimension: TwoOrThreeDimension?,
) : Parcelable {
    override fun toString(): String {
        if (twoOrThreeDimension == null) {
            return standalone.toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        }
        val length = twoOrThreeDimension.length.toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        val width = twoOrThreeDimension.width.toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
        return buildString {
            append(length)
            append('x')
            append(width)
            val height =
                twoOrThreeDimension.height?.toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
            if (!height.isNullOrBlank()) {
                append('x')
                append(height)
            }
        }
    }

    @Serializable
    @Parcelize
    data class TwoOrThreeDimension(
        val length: Float,
        val width: Float,
        val height: Float?,
    ) : Parcelable {
        companion object {
            val default = TwoOrThreeDimension(
                length = 1f,
                width = 1f,
                height = 1f,
            )
        }
    }

    companion object {
        val default: MeasurementUnitQuantity = MeasurementUnitQuantity(
            standalone = 1f,
            twoOrThreeDimension = null,
        )
    }
}