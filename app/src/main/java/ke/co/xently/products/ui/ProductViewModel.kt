package ke.co.xently.products.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.products.models.Product
import ke.co.xently.products.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val product = MutableStateFlow(Product.LocalViewModel.default)
    private val addProductState = MutableStateFlow<State>(State.Idle)

    fun savePermanently(product: Product.LocalViewModel) {
        saveDraft(product)

        viewModelScope.launch {
            this@ProductViewModel.product.map(repository::addProduct).onStart {
                addProductState.value = State.Loading
            }.collectLatest { result ->
                result.onSuccess {
                    addProductState.value = State.Success(it)
                }.onFailure {
                    addProductState.value = State.Failure(it)
                }
            }
        }
    }

    fun saveDraft(product: Product.LocalViewModel) {
        this.product.value = product
    }
}
