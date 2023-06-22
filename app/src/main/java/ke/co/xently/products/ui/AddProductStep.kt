package ke.co.xently.products.ui

enum class AddProductStep {
    Store,
    Shop,
    ProductName,
    GeneralDetails,
    MeasurementUnit,
    Brands,
    Attributes;

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