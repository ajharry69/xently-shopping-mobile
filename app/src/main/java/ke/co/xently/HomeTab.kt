package ke.co.xently

import androidx.annotation.StringRes

enum class HomeTab(@StringRes val title: Int) {
    AddProducts(R.string.add_products),
    GetRecommendations(R.string.get_recommendations)
}