package ke.co.xently.shopping.features.store.models

import android.content.Context
import android.os.Parcelable
import androidx.annotation.Keep
import ke.co.xently.shopping.features.core.models.Location
import ke.co.xently.shopping.features.core.numberFormat
import ke.co.xently.shopping.features.shop.models.Shop
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

sealed interface Store {
    val slug: String
    val name: String
    val location: Location
    val shop: Shop
    fun hasAnOnlineStore() = shop.hasAnOnlineStore()

    @Keep
    data class RemoteRequest(
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteRequest,
    ) : Store

    @Keep
    @Serializable
    data class RemoteResponse(
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteResponse,
    ) : Store

    @Keep
    data class LocalEntityRequest(
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityRequest,
    ) : Store

    @Keep
    data class LocalEntityResponse(
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityResponse,
    ) : Store

    @Keep
    @Parcelize
    @Serializable
    data class LocalViewModel(
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalViewModel,
        /**
         * This is in meters
         */
        val distance: Double?,
    ) : Store, Parcelable {
        fun getDistanceForDisplay(context: Context): String? {
            return distance?.let {
                buildString {
                    append(context.numberFormat.format(it.roundToInt()))
                    append('m')
                }
            }
        }

        fun isRedacted(): Boolean {
            return toString().let {
                it.startsWith("xxx")
                        && it.endsWith("xxx")
            }
        }

        override fun toString(): String {
            return buildString {
                if (name.isNotBlank()) {
                    append(name)
                }

                shop.toString().takeIf {
                    it.isNotBlank()
                }?.let {
                    append(", ")
                    append(it)
                }
            }
        }

        companion object {
            val default = LocalViewModel(
                slug = "",
                name = "",
                location = Location.default,
                shop = Shop.LocalViewModel.default,
                distance = null,
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }
        }
    }


    fun toRemoteResponse(): RemoteResponse {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is RemoteResponse -> {
                this
            }
        }
    }


    fun toLocalEntityRequest(): LocalEntityRequest {
        return when (this) {
            is LocalEntityRequest -> {
                this
            }

            is LocalEntityResponse -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }
        }
    }


    fun toLocalEntityResponse(): LocalEntityResponse {
        return when (this) {
            is LocalEntityRequest -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }
        }
    }


    fun toLocalViewModel(): LocalViewModel {
        return when (this) {
            is LocalEntityRequest -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }
        }
    }
}
