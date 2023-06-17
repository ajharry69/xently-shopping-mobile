package ke.co.xently.products.models

sealed interface Shop {
    val id: Long
    val name: String

    data class RemoteRequest(
        override val id: Long,
        override val name: String,
    ) : Shop

    data class RemoteResponse(
        override val id: Long,
        override val name: String,
    ) : Shop

    data class LocalEntityRequest(
        override val id: Long,
        override val name: String,
    ) : Shop

    data class LocalEntityResponse(
        override val id: Long,
        override val name: String,
    ) : Shop


    data class LocalViewModel(
        override val id: Long,
        override val name: String,
    ) : Shop {
        companion object {
            val default = LocalViewModel(id = -1, name = "")
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    name = name,
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    name = name,
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name,
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name,
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
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
                    id = id,
                    name = name,
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
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
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    name = name,
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                )
            }
        }
    }
}
