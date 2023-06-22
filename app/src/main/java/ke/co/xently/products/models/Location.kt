package ke.co.xently.products.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val lat: Double,
    val lon: Double,
) : Parcelable {
    fun isUsable(): Boolean {
        return !(lat.isNaN() && lon.isNaN())
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (lat != other.lat) return false
        if (lon != other.lon) return false

        return true
    }

    fun toLatLng(): LatLng {
        return LatLng(lat, lon)
    }

    companion object {
        val default = Location(
            lat = Double.NaN,
            lon = Double.NaN,
        )
    }
}

fun android.location.Location.toLocation(): Location {
    return Location(lat = latitude, lon = longitude)
}

fun LatLng.toLocation(): Location {
    return Location(lat = latitude, lon = longitude)
}
