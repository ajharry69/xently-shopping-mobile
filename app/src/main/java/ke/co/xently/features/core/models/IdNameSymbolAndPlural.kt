package ke.co.xently.features.core.models

interface IdNameSymbolAndPlural {
    val id: Long
    val name: String
    val namePlural: String?

    /**
     * For example, (") can be provided as a symbol for inches.
     */
    val symbol: String?
    val symbolPlural: String?
}