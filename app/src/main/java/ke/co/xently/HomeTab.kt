package ke.co.xently

import androidx.annotation.StringRes

enum class HomeTab(@StringRes val title: Int) {
    Compare(R.string.xently_home_tab_title_compare),
    Recommendations(R.string.xently_home_tab_title_recommendations),
    AddProducts(R.string.xently_home_tab_title_products),
}