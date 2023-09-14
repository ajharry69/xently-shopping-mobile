package ke.co.xently.shopping.features.measurementunit.models

import android.os.Parcelable
import androidx.annotation.Keep
import ke.co.xently.shopping.features.core.models.SynonymStructure
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface MeasurementUnit : SynonymStructure {
    @Keep
    data class RemoteRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : MeasurementUnit

    @Keep
    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : MeasurementUnit

    @Keep
    data class LocalEntityRequest(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : MeasurementUnit

    @Keep
    data class LocalEntityResponse(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : MeasurementUnit

    @Keep
    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val slug: String,
        override val name: String,
        override val plural: String?,
        override val symbol: String?,
    ) : MeasurementUnit, Parcelable {
        override fun toString(): String {
            return name
        }

        companion object {
            val default = LocalViewModel(
                id = -1,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
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
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
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
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
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
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
                    name = name,
                    plural = plural,
                    symbol = symbol,
                )
            }
        }
    }
}
