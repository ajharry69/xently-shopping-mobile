package ke.co.xently.shopping.features.attributes.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Attribute {
    val slug: String
    val name: String

    @Keep
    data class RemoteRequest(
        override val slug: String,
        override val name: String,
    ) : Attribute

    @Keep
    @Serializable
    data class RemoteResponse(
        override val slug: String,
        override val name: String,
    ) : Attribute

    @Keep
    data class LocalEntityRequest(
        override val slug: String,
        override val name: String,
    ) : Attribute

    @Keep
    data class LocalEntityResponse(
        override val slug: String,
        override val name: String,
    ) : Attribute

    @Keep
    @Parcelize
    data class LocalViewModel(
        override val slug: String,
        override val name: String,
    ) : Attribute, Parcelable {
        override fun hashCode(): Int {
            return name.lowercase().trim().hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LocalViewModel

            if (name.lowercase().trim() != other.name.lowercase().trim()) return false

            return true
        }

        override fun toString(): String {
            return name
        }

        companion object {
            val default = LocalViewModel(
                slug = "",
                name = "",
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    slug = slug,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    slug = slug,
                    name = name,
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
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    slug = slug,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    slug = slug,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    slug = slug,
                    name = name,
                )
            }
        }
    }
}
