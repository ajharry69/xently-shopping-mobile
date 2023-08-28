package ke.co.xently.shopping.features.shop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Shop {
    val id: Long
    val slug: String
    val name: String
    val ecommerceSiteUrl: String?

    fun hasAnOnlineStore(): Boolean {
        return !ecommerceSiteUrl.isNullOrBlank()
    }

    data class RemoteRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    data class LocalEntityRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    data class LocalEntityResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Parcelize
    @Serializable
    data class LocalViewModel(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop, Parcelable {
        override fun toString(): String {
            return name
        }

        companion object {
            val default = LocalViewModel(
                id = -1,
                slug = "",
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
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
                    slug = slug,
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
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
                    slug = slug,
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }
        }
    }
}
