package ke.co.xently.products.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface MeasurementUnit : IdNameSymbolAndPlural {

    data class RemoteRequest(
        override val id: Long,
        override val name: String,
        override val namePlural: String?,
        override val symbol: String?,
        override val symbolPlural: String?,
    ) : MeasurementUnit

    data class RemoteResponse(
        override val id: Long,
        override val name: String,
        override val namePlural: String?,
        override val symbol: String?,
        override val symbolPlural: String?,
    ) : MeasurementUnit

    data class LocalEntityRequest(
        override val id: Long,
        override val name: String,
        override val namePlural: String?,
        override val symbol: String?,
        override val symbolPlural: String?,
    ) : MeasurementUnit

    data class LocalEntityResponse(
        override val id: Long,
        override val name: String,
        override val namePlural: String?,
        override val symbol: String?,
        override val symbolPlural: String?,
    ) : MeasurementUnit

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val name: String,
        override val namePlural: String?,
        override val symbol: String?,
        override val symbolPlural: String?,
    ) : MeasurementUnit, Parcelable {
        companion object {
            val default = LocalViewModel(
                id = -1,
                name = "",
                namePlural = null,
                symbol = null,
                symbolPlural = null,
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
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
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
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
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
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
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
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
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    namePlural = namePlural,
                    symbol = symbol,
                    symbolPlural = symbolPlural,
                )
            }
        }
    }
}
