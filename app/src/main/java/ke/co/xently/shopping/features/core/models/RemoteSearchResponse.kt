package ke.co.xently.shopping.features.core.models

data class RemoteSearchResponse<T>(
    val _embedded: Embedded<T> = Embedded(),
    val _links: Links = Links(),
    val page: Page = Page(),
) {
    data class Embedded<T>(val viewModels: List<T> = emptyList())

    data class Links(val self: Self = Self()) {
        data class Self(val href: String = "")
    }

    data class Page(
        val size: Int = -1,
        val totalElements: Int = -1,
        val totalPages: Int = -1,
        val number: Int = -1,
    )
}