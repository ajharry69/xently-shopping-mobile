package ke.co.xently.shopping

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeTab(@StringRes val title: Int, val image: ImageVector) {
    Compare(R.string.xently_home_tab_title_compare, Icons.Default.Compare),
    Recommendations(R.string.xently_home_tab_title_recommend, Icons.Default.Recommend),
    AddProducts(R.string.xently_home_tab_title_add_product, Icons.Default.AddCard),
}