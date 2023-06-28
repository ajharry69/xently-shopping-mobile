package ke.co.xently.products.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.products.models.Attribute
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.models.Product
import ke.co.xently.products.models.ProductName
import ke.co.xently.products.models.Shop
import ke.co.xently.products.models.Store
import ke.co.xently.products.repositories.MeasurementUnitRepository
import ke.co.xently.products.repositories.ProductRepository
import ke.co.xently.products.repositories.ShopRepository
import ke.co.xently.products.repositories.StoreRepository
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
    private val stateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val shopRepository: ShopRepository,
    private val measurementUnitRepository: MeasurementUnitRepository,
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
    private val mutableBrandSuggestionsState = MutableStateFlow<List<Brand>>(emptyList())

    val brandSuggestions = mutableBrandSuggestionsState.asStateFlow()

    private val mutableAttributeValueSuggestionsState =
        MutableStateFlow<List<AttributeValue>>(emptyList())

    val attributeValueSuggestions = mutableAttributeValueSuggestionsState.asStateFlow()

    private val mutableAttributeSuggestionsState = MutableStateFlow<List<Attribute>>(emptyList())

    val attributeSuggestions = mutableAttributeSuggestionsState.asStateFlow()

    private val mutableStoreSuggestionsState = MutableStateFlow<List<Store>>(emptyList())

    val storeSuggestions = mutableStoreSuggestionsState.asStateFlow()

    private val mutableShopSuggestionsState = MutableStateFlow<List<Shop>>(emptyList())

    val shopSuggestions = mutableShopSuggestionsState.asStateFlow()

    private val mutableProductSuggestionsState = MutableStateFlow<List<Product>>(emptyList())

    val productSuggestions = mutableProductSuggestionsState.asStateFlow()

    private val mutableMeasurementUnitSuggestionsState =
        MutableStateFlow<List<MeasurementUnit>>(emptyList())

    val measurementUnitSuggestions = mutableMeasurementUnitSuggestionsState.asStateFlow()

    private val mutableSaveProductState = MutableStateFlow<State>(State.Idle)

    val saveProductState = mutableSaveProductState.asStateFlow()

    fun saveCurrentlyActiveStep(step: AddProductStep) {
        stateHandle[CURRENT_ACTIVE_STEP_KEY] = step
    }

    fun savePermanently(stepsToPersist: Array<AddProductStep>) {
        viewModelScope.launch {
            this@ProductViewModel.product.map(productRepository::addProduct).onStart {
                mutableSaveProductState.value = State.Loading
            }.collectLatest { result ->
                result.onSuccess {
                    var newDraftProduct = Product.LocalViewModel.default
                    for (step in stepsToPersist) {
                        when (step) {
                            AddProductStep.Store -> {
                                newDraftProduct = newDraftProduct.copy(store = it.store)
                            }

                            AddProductStep.Shop -> {
                                newDraftProduct = newDraftProduct.run {
                                    copy(store = store.copy(shop = it.store.shop))
                                }
                            }

                            AddProductStep.ProductName -> {
                                newDraftProduct = newDraftProduct.copy(name = it.name)
                            }

                            AddProductStep.GeneralDetails -> {
                                newDraftProduct = newDraftProduct.copy(
                                    unitPrice = it.unitPrice,
                                    packCount = it.packCount,
                                    datePurchased = it.datePurchased,
                                )
                            }

                            AddProductStep.MeasurementUnit -> {
                                newDraftProduct = newDraftProduct.copy(
                                    measurementUnit = it.measurementUnit,
                                    measurementUnitQuantity = it.measurementUnitQuantity,
                                )
                            }

                            AddProductStep.Brands -> {
                                newDraftProduct = newDraftProduct.copy(brands = it.brands)
                            }

                            AddProductStep.Attributes -> {
                                newDraftProduct = newDraftProduct.copy(attributes = it.attributes)
                            }

                            AddProductStep.Summary -> {

                            }
                        }
                    }
                    saveDraft(newDraftProduct)
                    mutableSaveProductState.value = State.Success(it)
                }.onFailure {
                    mutableSaveProductState.value = State.Failure(it)
                }
            }
        }
    }

    fun saveDraft(product: Product) {
        stateHandle[CURRENT_PRODUCT_KEY] = product.toLocalViewModel()
    }

    fun searchAttributeValue(attribute: AttributeValue) {
        mutableAttributeValueSuggestionsState.value =
            listOf(attribute) + List(Random(0).nextInt(5)) {
                attribute.toLocalViewModel()
                    .copy(value = buildString { append(attribute.value); append(it + 1) })
            }
    }

    fun searchAttribute(attribute: Attribute) {
        mutableAttributeSuggestionsState.value = listOf(attribute) + List(Random(0).nextInt(5)) {
            attribute.toLocalViewModel()
                .copy(name = buildString { append(attribute.name); append(it + 1) })
        }
    }

    fun searchBrand(brand: Brand) {
        mutableBrandSuggestionsState.value = listOf(brand) + List(Random(0).nextInt(5)) {
            brand.toLocalViewModel().copy(name = buildString { append(brand.name); append(it + 1) })
        }
    }

    fun searchStore(store: Store) {
        viewModelScope.launch {
            storeRepository.getStoreSearchSuggestions(query = store)
                .onSuccess {
                    mutableStoreSuggestionsState.value = listOf(store) + it
                }.onFailure {
                    Log.e(TAG, "searchStore: ${it.localizedMessage}", it)
                    mutableStoreSuggestionsState.value = listOf(store)
                }
        }
    }

    fun searchShop(shop: Shop) {
        viewModelScope.launch {
            shopRepository.getShopSearchSuggestions(query = shop)
                .onSuccess {
                    mutableShopSuggestionsState.value = listOf(shop) + it
                }.onFailure {
                    Log.e(TAG, "searchShop: ${it.localizedMessage}", it)
                    mutableShopSuggestionsState.value = listOf(shop)
                }
        }
    }

    fun searchProductName(name: ProductName) {
        viewModelScope.launch {
            val query = Product.LocalViewModel.default.copy(name = name.toLocalViewModel())
            productRepository.getProductSearchSuggestions(query = query)
                .onSuccess {
                    mutableProductSuggestionsState.value = listOf(query) + it
                }.onFailure {
                    Log.e(TAG, "searchProductName: ${it.localizedMessage}", it)
                    mutableProductSuggestionsState.value = listOf(query)
                }
        }
    }

    fun searchMeasurementUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            measurementUnitRepository.getMeasurementUnitSearchSuggestions(query = unit)
                .onSuccess {
                    mutableMeasurementUnitSuggestionsState.value = listOf(unit) + it
                }.onFailure {
                    Log.e(TAG, "searchMeasurementUnit: ${it.localizedMessage}", it)
                    mutableMeasurementUnitSuggestionsState.value = listOf(unit)
                }
        }
    }

    fun clearShopSearchSuggestions() {
        mutableShopSuggestionsState.value = emptyList()
    }

    fun clearStoreSearchSuggestions() {
        mutableStoreSuggestionsState.value = emptyList()
    }

    fun clearProductSearchSuggestions() {
        mutableProductSuggestionsState.value = emptyList()
    }

    fun clearMeasurementUnitSearchSuggestions() {
        mutableMeasurementUnitSuggestionsState.value = emptyList()
    }

    fun clearBrandSearchSuggestions() {
        mutableBrandSuggestionsState.value = emptyList()
    }

    fun clearAttributeValueSuggestions() {
        mutableAttributeValueSuggestionsState.value = emptyList()
    }

    fun clearAttributeSuggestions() {
        mutableAttributeSuggestionsState.value = emptyList()
    }

    override fun onCleared() {
        clearShopSearchSuggestions()
        clearShopSearchSuggestions()
        clearProductSearchSuggestions()
        clearMeasurementUnitSearchSuggestions()
        clearAttributeSuggestions()
        clearAttributeValueSuggestions()
        super.onCleared()
    }
}
