package ke.co.xently.products.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.models.Product
import ke.co.xently.products.models.ProductName
import ke.co.xently.products.models.Shop
import ke.co.xently.products.models.Store
import ke.co.xently.products.repositories.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private val TAG = ProductViewModel::class.java.simpleName
        private val CURRENT_ACTIVE_STEP_KEY = TAG.plus("CURRENT_ACTIVE_STEP_KEY")
        private val CURRENT_PRODUCT_KEY = TAG.plus("CURRENT_PRODUCT_KEY")
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

    private val storeSuggestionsMutable = MutableStateFlow<List<Store>>(emptyList())

    val storeSuggestions = storeSuggestionsMutable.asStateFlow()

    private val shopSuggestionsMutable = MutableStateFlow<List<Shop>>(emptyList())

    val shopSuggestions = shopSuggestionsMutable.asStateFlow()

    private val productSuggestionsMutable = MutableStateFlow<List<Product>>(emptyList())

    val productSuggestions = productSuggestionsMutable.asStateFlow()

    private val measurementUnitSuggestionsMutable =
        MutableStateFlow<List<MeasurementUnit>>(emptyList())

    val measurementUnitSuggestions = measurementUnitSuggestionsMutable.asStateFlow()

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
        attributeSuggestionsMutable.value = listOf(attribute) + List(Random(0).nextInt(5)) {
            attribute.toLocalViewModel()
                .copy(value = buildString { append(attribute.value); append(it + 1) })
        }
    }

    fun searchBrand(brand: Brand) {
        brandSuggestionsMutable.value = listOf(brand) + List(Random(0).nextInt(5)) {
            brand.toLocalViewModel().copy(name = buildString { append(brand.name); append(it + 1) })
        }
    }

    fun searchStore(store: Store) {
        storeSuggestionsMutable.value = listOf(store) + List(Random.nextInt(0, 10)) {
            store.toLocalViewModel().copy(
                name = buildString { append(store.name); if (!endsWith(' ')) append(' '); append(it + 1) },
                shop = store.shop.toLocalViewModel().copy(
                    name = "Shop name ${Random.nextInt()}"
                ),
            )
        }
    }

    fun clearStoreSearchSuggestions() {
        storeSuggestionsMutable.value = emptyList()
    }

    fun searchShop(shop: Shop) {
        shopSuggestionsMutable.value = listOf(shop) + List(Random.nextInt(0, 10)) {
            shop.toLocalViewModel().copy(
                name = buildString { append(shop.name); if (!endsWith(' ')) append(' '); append(it + 1) },
            )
        }
    }

    fun clearShopSearchSuggestions() {
        shopSuggestionsMutable.value = emptyList()
    }

    fun searchProductName(name: ProductName) {
        productSuggestionsMutable.value =
            listOf(Product.LocalViewModel.default.copy(name = name.toLocalViewModel())) + List(
                Random.nextInt(0, 10)
            ) {
                val productName = name.toLocalViewModel().copy(name = buildString {
                    append(name.name)
                    if (!endsWith(' ')) {
                        append(' ')
                    }
                    append(it + 1)
                })
                Product.LocalViewModel.default.copy(name = productName)
            }
    }

    fun clearProductSearchSuggestions() {
        productSuggestionsMutable.value = emptyList()
    }

    fun searchMeasurementUnit(unit: MeasurementUnit) {
        measurementUnitSuggestionsMutable.value = listOf(unit) + List(
            Random.nextInt(0, 10)
        ) {
            val name = buildString {
                append(unit.name)
                if (!endsWith(' ')) {
                    append(' ')
                }
                append(it + 1)
            }
            unit.toLocalViewModel().copy(name = name)
        }
    }

    fun clearMeasurementUnitSearchSuggestions() {
        measurementUnitSuggestionsMutable.value = emptyList()
    }
}
