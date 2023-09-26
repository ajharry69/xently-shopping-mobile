package ke.co.xently.shopping.features.shop.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Shop {
    val slug: String
    val name: String
    val ecommerceSiteUrl: String?

    fun hasAnOnlineStore(): Boolean {
        return !ecommerceSiteUrl.isNullOrBlank()
    }

    @Keep
    data class RemoteRequest(
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Keep
    @Serializable
    data class RemoteResponse(
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Keep
    data class LocalEntityRequest(
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Keep
    data class LocalEntityResponse(
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop

    @Keep
    @Parcelize
    @Serializable
    data class LocalViewModel(
        override val slug: String,
        override val name: String,
        override val ecommerceSiteUrl: String?,
    ) : Shop, Parcelable {
        override fun toString(): String {
            return name
        }

        companion object {
            val default = LocalViewModel(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
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
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    ecommerceSiteUrl = ecommerceSiteUrl,
                )
            }
        }
    }
}
