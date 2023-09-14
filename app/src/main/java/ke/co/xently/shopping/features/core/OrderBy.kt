package ke.co.xently.shopping.features.core

import androidx.annotation.Keep
import androidx.annotation.StringRes
import ke.co.xently.shopping.R

@Keep
enum class OrderBy(@StringRes val label: Int) {
    Ascending(R.string.xently_order_by_ascending),
    Descending(R.string.xently_order_by_descending);

    val opposite: OrderBy
        get() = when (this) {
            Ascending -> Descending
            Descending -> Ascending
        }
}