package ke.co.xently.shopping.features.core.models

interface SynonymStructure {
    val id: Long
    val name: String
    val slug: String
    val plural: String?

    /**
     * For example, (") can be provided as a symbol for inches.
     */
    val symbol: String?
}