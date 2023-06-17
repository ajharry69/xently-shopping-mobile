package ke.co.xently.products.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.products.models.Product
import ke.co.xently.products.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {
    fun savePermanently(product: Product.LocalViewModel) {

    }

    fun saveDraft(product: Product.LocalViewModel) {

    }

    private val product = MutableStateFlow(Product.LocalViewModel.default)
}
