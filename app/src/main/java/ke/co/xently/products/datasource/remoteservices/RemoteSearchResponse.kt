package ke.co.xently.products.datasource.remoteservices

data class RemoteSearchResponse<T>(
    val _embedded: Embedded<T>,
    val _links: Links,
    val page: Page,
) {
    data class Embedded<T>(val viewModels: List<T>)

    data class Links(val self: Self) {
        data class Self(val href: String)
    }

    data class Page(
        val size: Int,
        val totalElements: Int,
        val totalPages: Int,
        val number: Int
    )
}