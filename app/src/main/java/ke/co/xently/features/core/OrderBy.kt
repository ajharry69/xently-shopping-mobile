package ke.co.xently.features.core

import androidx.annotation.StringRes
import ke.co.xently.R

enum class OrderBy(@StringRes val label: Int) {
    Ascending(R.string.xently_order_by_ascending),
    Descending(R.string.xently_order_by_descending);

    val opposite: OrderBy
        get() = when (this) {
            Ascending -> Descending
            Descending -> Ascending
        }
}