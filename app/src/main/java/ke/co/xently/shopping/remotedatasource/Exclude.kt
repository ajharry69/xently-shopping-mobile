package ke.co.xently.shopping.remotedatasource

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
/**
 * Excludes field(s) from json serialization and/or deserialization depending on [during]
 * @param during When to exclude field from serialization and/or deserialization
 */
annotation class Exclude(val during: During = During.BOTH) {
    /**
     * @see SERIALIZATION Exclude field ONLY from json serialization
     * @see DESERIALIZATION Exclude field ONLY from json deserialization
     * @see BOTH Exclude field from json serialization and deserialization
     */
    enum class During {
        /**
         * Exclude field ONLY from json serialization
         */
        SERIALIZATION,

        /**
         * Exclude field ONLY from json deserialization
         */
        DESERIALIZATION,

        /**
         * Exclude field from json serialization and deserialization
         */
        BOTH
    }
}