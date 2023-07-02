package ke.co.xently.features.shop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Shop {
    val id: Long
    val name: String
    val ecommerceSiteUrl: String?

    data class RemoteRequest(
        override val id: Long,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    data class RemoteResponse(
        override val id: Long,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    data class LocalEntityRequest(
        override val id: Long,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    data class LocalEntityResponse(
        override val id: Long,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop, Parcelable {
        override fun toString(): String {
            return name
        }

        companion object {
            val default = LocalViewModel(
                id = -1,
                name = "",
                ecommerceSiteUrl = null,
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
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
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
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
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
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
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
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
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }
        }
    }
}
