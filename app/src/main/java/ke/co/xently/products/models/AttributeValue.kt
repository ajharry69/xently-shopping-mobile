package ke.co.xently.products.models

sealed interface AttributeValue {
    val id: Long
    val name: String
    val attribute: Attribute

    data class RemoteRequest(
        override val id: Long,
        override val name: String,
        override val attribute: Attribute.RemoteRequest,
    ) : AttributeValue

    data class RemoteResponse(
        override val id: Long,
        override val name: String,
        override val attribute: Attribute.RemoteResponse,
    ) : AttributeValue

    data class LocalEntityRequest(
        override val id: Long,
        override val name: String,
        override val attribute: Attribute.LocalEntityRequest,
    ) : AttributeValue

    data class LocalEntityResponse(
        override val id: Long,
        override val name: String,
        override val attribute: Attribute.LocalEntityResponse,
    ) : AttributeValue


    data class LocalViewModel(
        override val id: Long,
        override val name: String,
        override val attribute: Attribute.LocalViewModel,
    ) : AttributeValue


    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    name = name,
                    attribute = attribute.toRemoteRequest(),
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    name = name,
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
                    name = name,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name,
                    attribute = attribute.toRemoteResponse(),
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    name = name,
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
                    name = name,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalEntityRequest(),
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    name = name,
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
                    name = name,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalEntityResponse(),
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    name = name,
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
                    name = name,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalViewModel(),
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name,
                    attribute = attribute.toLocalViewModel(),
                )
            }
        }
    }
}
