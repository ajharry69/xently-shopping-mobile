package ke.co.xently.shopping.features.attributesvalues.models

import android.os.Parcelable
import ke.co.xently.shopping.features.attributes.models.Attribute
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface AttributeValue {
    val id: Long
    val value: String
    val attribute: Attribute

    data class RemoteRequest(
        override val id: Long,
        override val value: String,
        override val attribute: Attribute.RemoteRequest,
    ) : AttributeValue

    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val value: String,
        override val attribute: Attribute.RemoteResponse,
    ) : AttributeValue

    data class LocalEntityRequest(
        override val id: Long,
        override val value: String,
        override val attribute: Attribute.LocalEntityRequest,
    ) : AttributeValue

    data class LocalEntityResponse(
        override val id: Long,
        override val value: String,
        override val attribute: Attribute.LocalEntityResponse,
    ) : AttributeValue

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val value: String,
        override val attribute: Attribute.LocalViewModel,
    ) : AttributeValue, Parcelable {
        override fun toString(): String {
            return "$attribute:$value"
        }

        override fun hashCode(): Int {
            var result = value.lowercase().trim().hashCode()
            result = 31 * result + attribute.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LocalViewModel

            if (value.lowercase().trim() != other.value.lowercase().trim()) return false
            if (attribute != other.attribute) return false

            return true
        }

        companion object {
            val default = LocalViewModel(
                id = -1,
                value = "",
                attribute = Attribute.LocalViewModel.default,
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteRequest(),
                )
            }
        }
    }


    fun toRemoteResponse(): RemoteResponse {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toRemoteResponse(),
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
                    value = value,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }
        }
    }


    fun toLocalEntityResponse(): LocalEntityResponse {
        return when (this) {
            is LocalEntityRequest -> {
                LocalEntityResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }
        }
    }


    fun toLocalViewModel(): LocalViewModel {
        return when (this) {
            is LocalEntityRequest -> {
                LocalViewModel(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    value = value,
                    attribute = attribute.toLocalViewModel(),
                )
            }
        }
    }
}
