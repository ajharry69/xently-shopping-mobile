package ke.co.xently.features.products.models

import android.content.Context
import android.os.Parcelable
import ke.co.xently.R
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.features.core.currencyNumberFormat
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.features.store.models.Store
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed interface Product {
    val id: Long
    val name: ProductName
    val descriptiveName: String
    val store: Store
    val packCount: Int

    // A face-value unit that affects the price of a product. For example,
    // a 21-inch TV should be less expensive compared to a 32-inch TV. The
    // same apply to 200-liter refrigerator and a 300-liter refrigerator.
    val measurementUnit: MeasurementUnit?
    val measurementUnitQuantity: Float
    val unitPrice: BigDecimal
    val brands: List<Brand>
    val attributes: List<AttributeValue>

    fun buildDescriptiveName(context: Context, locale: Locale): String {
        val descriptiveName = buildString {
            attributes.map { it.value.lowercase(locale) }.sorted().also { attrs ->
                if (attrs.isEmpty()) {
                    append(name.name.replaceFirstChar { it.uppercaseChar() })
                } else {
                    append(attrs.joinToString().replaceFirstChar { it.uppercaseChar() })
                    append(" ")
                    append(name.name.lowercase(locale))
                }
            }
            brands.map { it.name }.sorted().also {
                when (val count = it.size) {
                    0 -> {}
                    1 -> {
                        append(' ')
                        append(context.getString(R.string.xently_description_by))
                        append(' ')
                        append(it[0])
                    }

                    else -> {
                        append(' ')
                        append(context.getString(R.string.xently_description_by))
                        append(' ')
                        append(it.take(count - 1).joinToString())
                        append(' ')
                        append(context.getString(R.string.xently_description_and))
                        append(' ')
                        append(it.last())
                    }
                }
            }
        }

        return buildString {
            append(packCount)
            append(' ')
            append(context.getString(R.string.xently_description_pack_of))
            append(' ')
            append(descriptiveName.replaceFirstChar { it.lowercase(locale) })
            append(", ")
            append(context.getString(R.string.xently_description_purchased_at))
            append(' ')
            context.currencyNumberFormat
                .format(unitPrice)
                .let(::append)
            append("/=")
            if (measurementUnit != null) {
                append(' ')
                append(context.getString(R.string.xently_description_per))
                append(' ')
                append(measurementUnitQuantity)
                append("-")
                append(measurementUnit!!.name.lowercase(locale))
            }
            store.toString().takeIf {
                it.isNotBlank()
            }?.let {
                append(", ")
                append(context.getString(R.string.xently_description_from))
                append(' ')
                append(it)
            }
        }
    }

    data class RemoteRequest(
        override val id: Long,
        override val name: ProductName.RemoteRequest,
        override val descriptiveName: String,
        override val store: Store.RemoteRequest,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.RemoteRequest?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.RemoteRequest>,
        override val attributes: List<AttributeValue.RemoteRequest>,
        val datePurchased: String,
    ) : Product {
        companion object {
            val DATE_TIME_PURCHASED_FORMAT: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        }
    }

    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val name: ProductName.RemoteResponse,
        override val descriptiveName: String,
        override val store: Store.RemoteResponse,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.RemoteResponse?,
        override val measurementUnitQuantity: Float,
        @Contextual // TODO: Find a better serialization strategy
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.RemoteResponse>,
        override val attributes: List<AttributeValue.RemoteResponse>,
        val datePurchased: String,
    ) : Product {
        companion object {
            val DATE_TIME_PURCHASED_FORMAT: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        }
    }

    data class LocalEntityRequest(
        override val id: Long,
        override val name: ProductName.LocalEntityRequest,
        override val descriptiveName: String,
        override val store: Store.LocalEntityRequest,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalEntityRequest?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalEntityRequest>,
        override val attributes: List<AttributeValue.LocalEntityRequest>,
        val datePurchased: LocalDateTime,
    ) : Product

    data class LocalEntityResponse(
        override val id: Long,
        override val name: ProductName.LocalEntityResponse,
        override val descriptiveName: String,
        override val store: Store.LocalEntityResponse,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalEntityResponse?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalEntityResponse>,
        override val attributes: List<AttributeValue.LocalEntityResponse>,
        val datePurchased: LocalDateTime,
    ) : Product

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val name: ProductName.LocalViewModel,
        override val descriptiveName: String,
        override val store: Store.LocalViewModel,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalViewModel?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalViewModel>,
        override val attributes: List<AttributeValue.LocalViewModel>,
        val datePurchased: LocalDateTime,
        val autoFillNamePlural: Boolean = false,
        val autoFillMeasurementUnitNamePlural: Boolean = false,
        val autoFillMeasurementUnitSymbolPlural: Boolean = false,
    ) : Product, Parcelable {
        companion object {
            val default = LocalViewModel(
                id = -1,
                name = ProductName.LocalViewModel.default,
                descriptiveName = "",
                store = Store.LocalViewModel.default,
                packCount = 1,
                measurementUnit = null,
                measurementUnitQuantity = 1f,
                unitPrice = BigDecimal.ZERO,
                datePurchased = LocalDateTime.now(),
                brands = emptyList(),
                attributes = emptyList(),
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributes = attributes.map { it.toRemoteRequest() },
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributes = attributes.map { it.toRemoteRequest() },
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributes = attributes.map { it.toRemoteRequest() },
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toRemoteRequest() },
                    attributes = attributes.map { it.toRemoteRequest() },
                )
            }
        }
    }


    fun toRemoteResponse(): RemoteResponse {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteResponse(
                    id = id,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributes = attributes.map { it.toRemoteResponse() },
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributes = attributes.map { it.toRemoteResponse() },
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributes = attributes.map { it.toRemoteResponse() },
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributes = attributes.map { it.toRemoteResponse() },
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
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributes = attributes.map { it.toLocalEntityRequest() },
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributes = attributes.map { it.toLocalEntityRequest() },
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributes = attributes.map { it.toLocalEntityRequest() },
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributes = attributes.map { it.toLocalEntityRequest() },
                )
            }
        }
    }


    fun toLocalEntityResponse(): LocalEntityResponse {
        return when (this) {
            is LocalEntityRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributes = attributes.map { it.toLocalEntityResponse() },
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributes = attributes.map { it.toLocalEntityResponse() },
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributes = attributes.map { it.toLocalEntityResponse() },
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT
                    ),
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributes = attributes.map { it.toLocalEntityResponse() },
                )
            }
        }
    }


    fun toLocalViewModel(): LocalViewModel {
        return when (this) {
            is LocalEntityRequest -> {
                LocalViewModel(
                    id = id,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalViewModel() },
                    attributes = attributes.map { it.toLocalViewModel() },
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalViewModel() },
                    attributes = attributes.map { it.toLocalViewModel() },
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalViewModel() },
                    attributes = attributes.map { it.toLocalViewModel() },
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = LocalDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalViewModel() },
                    attributes = attributes.map { it.toLocalViewModel() },
                )
            }
        }
    }
}
