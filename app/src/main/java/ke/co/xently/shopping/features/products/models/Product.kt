package ke.co.xently.shopping.features.products.models

import android.content.Context
import android.os.Parcelable
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue
import ke.co.xently.shopping.features.brands.models.Brand
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.models.BigDecimalSerializer
import ke.co.xently.shopping.features.measurementunit.models.MeasurementUnit
import ke.co.xently.shopping.features.store.models.Store
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed interface Product {
    val id: Long
    val slug: String
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
    val attributeValues: List<AttributeValue>

    fun buildDescriptiveName(context: Context, locale: Locale): String {
        val descriptiveName = buildString {
            attributeValues.map { it.value.lowercase(locale) }.sorted().also { attrs ->
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
                .replace(".00", "")
                .let(::append)
            append("/=")
            if (measurementUnit != null) {
                append(' ')
                append(context.getString(R.string.xently_description_per))
                append(' ')
                append(measurementUnitQuantity.toString().replace(".0", ""))
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
       override val slug: String,
        override val name: ProductName.RemoteRequest,
        override val descriptiveName: String,
        override val store: Store.RemoteRequest,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.RemoteRequest?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.RemoteRequest>,
        override val attributeValues: List<AttributeValue.RemoteRequest>,
        val datePurchased: String,
    ) : Product {
        companion object {
            val DATE_TIME_PURCHASED_FORMAT: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX")
        }
    }

    @Serializable
    data class RemoteResponse(
        override val id: Long,
        override val slug: String,
        override val name: ProductName.RemoteResponse,
        override val descriptiveName: String,
        override val store: Store.RemoteResponse,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.RemoteResponse?,
        override val measurementUnitQuantity: Float,
        @Serializable(with = BigDecimalSerializer::class)
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.RemoteResponse>,
        override val attributeValues: List<AttributeValue.RemoteResponse>,
        val datePurchased: String,
    ) : Product {
        companion object {
            val DATE_TIME_PURCHASED_FORMAT: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        }
    }

    data class LocalEntityRequest(
        override val id: Long,
        override val slug: String,
        override val name: ProductName.LocalEntityRequest,
        override val descriptiveName: String,
        override val store: Store.LocalEntityRequest,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalEntityRequest?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalEntityRequest>,
        override val attributeValues: List<AttributeValue.LocalEntityRequest>,
        val datePurchased: OffsetDateTime,
    ) : Product

    data class LocalEntityResponse(
        override val id: Long,
        override val slug: String,
        override val name: ProductName.LocalEntityResponse,
        override val descriptiveName: String,
        override val store: Store.LocalEntityResponse,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalEntityResponse?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalEntityResponse>,
        override val attributeValues: List<AttributeValue.LocalEntityResponse>,
        val datePurchased: OffsetDateTime,
    ) : Product

    @Parcelize
    data class LocalViewModel(
        override val id: Long,
        override val slug: String,
        override val name: ProductName.LocalViewModel,
        override val descriptiveName: String,
        override val store: Store.LocalViewModel,
        override val packCount: Int,
        override val measurementUnit: MeasurementUnit.LocalViewModel?,
        override val measurementUnitQuantity: Float,
        override val unitPrice: BigDecimal,
        override val brands: List<Brand.LocalViewModel>,
        override val attributeValues: List<AttributeValue.LocalViewModel>,
        val datePurchased: OffsetDateTime,
        val autoFillNamePlural: Boolean = false,
        val autoFillMeasurementUnitNamePlural: Boolean = false,
        val autoFillMeasurementUnitSymbolPlural: Boolean = false,
    ) : Product, Parcelable {
        companion object {
            val default = LocalViewModel(
                id = -1,
                slug = "",
                name = ProductName.LocalViewModel.default,
                descriptiveName = "",
                store = Store.LocalViewModel.default,
                packCount = 1,
                measurementUnit = null,
                measurementUnitQuantity = 1f,
                unitPrice = BigDecimal.ZERO,
                datePurchased = OffsetDateTime.now(),
                brands = emptyList(),
                attributeValues = emptyList(),
            )
        }
    }

    fun toRemoteRequest(): RemoteRequest {
        return when (this) {
            is LocalEntityRequest -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributeValues = attributeValues.map { it.toRemoteRequest() },
                )
            }

            is LocalEntityResponse -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributeValues = attributeValues.map { it.toRemoteRequest() },
                )
            }

            is LocalViewModel -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteRequest.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteRequest() },
                    attributeValues = attributeValues.map { it.toRemoteRequest() },
                )
            }

            is RemoteRequest -> {
                this
            }

            is RemoteResponse -> {
                RemoteRequest(
                    id = id,
                    slug = slug,
                    name = name.toRemoteRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toRemoteRequest() },
                    attributeValues = attributeValues.map { it.toRemoteRequest() },
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
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributeValues = attributeValues.map { it.toRemoteResponse() },
                )
            }

            is LocalEntityResponse -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributeValues = attributeValues.map { it.toRemoteResponse() },
                )
            }

            is LocalViewModel -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributeValues = attributeValues.map { it.toRemoteResponse() },
                )
            }

            is RemoteRequest -> {
                RemoteResponse(
                    id = id,
                    slug = slug,
                    name = name.toRemoteResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toRemoteResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toRemoteResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased.format(RemoteResponse.DATE_TIME_PURCHASED_FORMAT),
                    brands = brands.map { it.toRemoteResponse() },
                    attributeValues = attributeValues.map { it.toRemoteResponse() },
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
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributeValues = attributeValues.map { it.toLocalEntityRequest() },
                )
            }

            is LocalViewModel -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributeValues = attributeValues.map { it.toLocalEntityRequest() },
                )
            }

            is RemoteRequest -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributeValues = attributeValues.map { it.toLocalEntityRequest() },
                )
            }

            is RemoteResponse -> {
                LocalEntityRequest(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityRequest(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityRequest(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityRequest(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityRequest() },
                    attributeValues = attributeValues.map { it.toLocalEntityRequest() },
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
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributeValues = attributeValues.map { it.toLocalEntityResponse() },
                )
            }

            is LocalEntityResponse -> {
                this
            }

            is LocalViewModel -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributeValues = attributeValues.map { it.toLocalEntityResponse() },
                )
            }

            is RemoteRequest -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributeValues = attributeValues.map { it.toLocalEntityResponse() },
                )
            }

            is RemoteResponse -> {
                LocalEntityResponse(
                    id = id,
                    slug = slug,
                    name = name.toLocalEntityResponse(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalEntityResponse(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalEntityResponse(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT
                    ),
                    brands = brands.map { it.toLocalEntityResponse() },
                    attributeValues = attributeValues.map { it.toLocalEntityResponse() },
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
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalViewModel() },
                    attributeValues = attributeValues.map { it.toLocalViewModel() },
                )
            }

            is LocalEntityResponse -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = datePurchased,
                    brands = brands.map { it.toLocalViewModel() },
                    attributeValues = attributeValues.map { it.toLocalViewModel() },
                )
            }

            is LocalViewModel -> {
                this
            }

            is RemoteRequest -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteRequest.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalViewModel() },
                    attributeValues = attributeValues.map { it.toLocalViewModel() },
                )
            }

            is RemoteResponse -> {
                LocalViewModel(
                    id = id,
                    slug = slug,
                    name = name.toLocalViewModel(),
                    descriptiveName = descriptiveName,
                    store = store.toLocalViewModel(),
                    packCount = packCount,
                    measurementUnit = measurementUnit?.toLocalViewModel(),
                    measurementUnitQuantity = measurementUnitQuantity,
                    unitPrice = unitPrice,
                    datePurchased = OffsetDateTime.parse(
                        datePurchased,
                        RemoteResponse.DATE_TIME_PURCHASED_FORMAT,
                    ),
                    brands = brands.map { it.toLocalViewModel() },
                    attributeValues = attributeValues.map { it.toLocalViewModel() },
                )
            }
        }
    }
}
