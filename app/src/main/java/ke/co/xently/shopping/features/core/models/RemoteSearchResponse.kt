package ke.co.xently.shopping.features.core.models

import androidx.annotation.Keep

@Keep
data class RemoteSearchResponse<T>(
    val _embedded: Embedded<T> = Embedded(),
    val _links: Links = Links(),
    val page: Page = Page(),
) {
    @Keep
    data class Embedded<T>(val viewModels: List<T> = emptyList())

    @Keep
    data class Links(val self: Self = Self()) {
        @Keep
        data class Self(val href: String = "")
    }

    @Keep
    data class Page(
        val size: Int = -1,
        val totalElements: Int = -1,
        val totalPages: Int = -1,
        val number: Int = -1,
    )
}