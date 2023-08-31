package ke.co.xently.shopping.features.products.ui

import androidx.annotation.StringRes
import ke.co.xently.shopping.R

enum class AddProductStep(@StringRes val title: Int) {
    Store(R.string.xently_add_store_page_title),
    Shop(R.string.xently_add_shop_page_title),
    ProductName(R.string.xently_product_name_page_title),
    GeneralDetails(R.string.xently_general_details_page_title),
    MeasurementUnitName(R.string.xently_measurement_unit_page_title),
    MeasurementUnitQuantity(R.string.xently_measurement_unit_quantity_page_title),
    Brands(R.string.xently_add_brands_page_title),
    Attributes(R.string.xently_add_attributes_page_title),
    Summary(R.string.xently_summary_page_title);

    companion object {
        private val MAPPED_STAGES = values().groupBy {
            it.ordinal
        }.mapValues {
            it.value[0]
        }

        private fun valueOf(ordinal: Int): AddProductStep? {
            return MAPPED_STAGES[ordinal]
        }

        fun valueOfOrdinalOrFirstByOrdinal(ordinal: Int): AddProductStep {
            return valueOf(ordinal) ?: valueOf(0)!!
        }
    }
}