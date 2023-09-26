package ke.co.xently.shopping.features.products.models

import android.os.Parcelable
import androidx.annotation.Keep
import ke.co.xently.shopping.features.core.models.SynonymStructure
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface ProductName : SynonymStructure {
    @Keep
    data class RemoteRequest(
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : ProductName

    @Keep
    @Serializable
    data class RemoteResponse(
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : ProductName

    @Keep
    data class LocalEntityRequest(
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : ProductName

    @Keep
    data class LocalEntityResponse(
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : ProductName

    @Keep
    @Parcelize
    data class LocalViewModel(
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : ProductName, Parcelable {
        companion object {
            val default = LocalViewModel(
                slug = "",
                name = "",
                plural = null,
                symbol = null,
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
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
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
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
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
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
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
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
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }
        }
    }
}
