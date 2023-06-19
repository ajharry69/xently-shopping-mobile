package ke.co.xently.products.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.Product
import ke.co.xently.products.repositories.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private val TAG = ProductViewModel::class.java.simpleName
        private val CURRENT_ACTIVE_STEP_KEY =
            ProductViewModel::class.java.simpleName.plus("CURRENT_ACTIVE_STEP_KEY")
        private val CURRENT_PRODUCT_KEY =
            ProductViewModel::class.java.simpleName.plus("CURRENT_PRODUCT_KEY")
    }

    val currentlyActiveStep = stateHandle.getStateFlow(
        CURRENT_ACTIVE_STEP_KEY,
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(0),
    )
    val product = stateHandle.getStateFlow(
        CURRENT_PRODUCT_KEY,
        Product.LocalViewModel.default,
    )
    private val brandSuggestionsMutable = MutableStateFlow<List<Brand>>(emptyList())

    val brandSuggestions = brandSuggestionsMutable.asStateFlow()

    private val attributeSuggestionsMutable = MutableStateFlow<List<AttributeValue>>(emptyList())

    val attributeSuggestions = attributeSuggestionsMutable.asStateFlow()

    private val addProductStateMutable = MutableStateFlow<State>(State.Idle)

    val addProductState = addProductStateMutable.asStateFlow()

    fun saveCurrentlyActiveStep(step: AddProductStep) {
        stateHandle[CURRENT_ACTIVE_STEP_KEY] = step
    }

    fun savePermanently(product: Product.LocalViewModel) {
        saveDraft(product)

        viewModelScope.launch {
            this@ProductViewModel.product.map(repository::addProduct).onStart {
                addProductStateMutable.value = State.Loading
                delay(5000)
            }.collectLatest { result ->
                result.onSuccess {
                    addProductStateMutable.value = State.Success(it)
                }.onFailure {
                    addProductStateMutable.value = State.Failure(it)
                }
            }
        }
    }

    fun saveDraft(product: Product.LocalViewModel) {
        stateHandle[CURRENT_PRODUCT_KEY] = product
        Log.d(TAG, "saveDraft: $product")
    }

    fun searchAttribute(attribute: AttributeValue) {

    }

    fun searchBrand(brand: Brand) {

    }
}
