package ke.co.xently.products.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Store {
    val id: Long
    val name: String
    val location: Location
    val shop: Shop

    data class RemoteRequest(
        override val id: Long,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteRequest,
    ) : Store

    data class RemoteResponse(
        override val id: Long,
        override val name: String,
        override val location: Location,
        override val shop: Shop.RemoteResponse,
    ) : Store

    data class LocalEntityRequest(
        override val id: Long,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityRequest,
    ) : Store

    data class LocalEntityResponse(
        override val id: Long,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalEntityResponse,
    ) : Store

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val name: String,
        override val location: Location,
        override val shop: Shop.LocalViewModel,
        /**
         * This is in meters
         */
        val distance: Double?,
    ) : Store, Parcelable {
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
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toRemoteRequest(),
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
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
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toRemoteResponse(),
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
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
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityRequest(),
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
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
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toLocalEntityResponse(),
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
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
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
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
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    location = location,
                    shop = shop.toLocalViewModel(),
                    distance = null,
                )
            }
        }
    }
}
