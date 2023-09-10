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
    val id: Long
    val slug: String
    val name: String
    val location: Location
    val shop: Shop
    fun hasAnOnlineStore() = shop.hasAnOnlineStore()

    @Keep
    data class RemoteRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteRequest,
    ) : Store

    @Keep
    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteResponse,
    ) : Store

    @Keep
    data class LocalEntityRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityRequest,
    ) : Store

    @Keep
    data class LocalEntityResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityResponse,
    ) : Store

    @Keep
    @Parcelize
    @Serializable
    data class LocalViewModel(
        override val id: Long,
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
                id = -1,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
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
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
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
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
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
