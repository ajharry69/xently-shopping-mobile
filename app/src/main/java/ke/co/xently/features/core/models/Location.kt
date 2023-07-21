package ke.co.xently.features.core.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
) : Parcelable {
    fun isUsable(): Boolean {
        return !(latitude.isNaN() && longitude.isNaN())
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    companion object {
        val default = Location(
            latitude = Double.NaN,
            longitude = Double.NaN,
        )
    }
}

fun android.location.Location.toLocation(): Location {
    return Location(latitude = latitude, longitude = longitude)
}

fun LatLng.toLocation(): Location {
    return Location(latitude = latitude, longitude = longitude)
}
