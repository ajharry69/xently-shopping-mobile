package ke.co.xently.features.products.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.attributes.models.Attribute
import ke.co.xently.features.attributes.repositories.AttributeRepository
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.attributesvalues.repositories.AttributeValueRepository
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.features.brands.repositories.BrandRepository
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.features.measurementunit.repositories.MeasurementUnitRepository
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.models.ProductName
import ke.co.xently.features.products.repositories.ProductRepository
import ke.co.xently.features.shop.models.Shop
import ke.co.xently.features.shop.repositories.ShopRepository
import ke.co.xently.features.store.models.Store
import ke.co.xently.features.store.repositories.StoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val shopRepository: ShopRepository,
    private val brandRepository: BrandRepository,
    private val attributeRepository: AttributeRepository,
    private val attributeValueRepository: AttributeValueRepository,
    private val measurementUnitRepository: MeasurementUnitRepository,
) : ViewModel() {
    companion object {
        private val TAG = ProductViewModel::class.java.simpleName
        private val CURRENT_ACTIVE_STEP_KEY = TAG.plus("CURRENT_ACTIVE_STEP_KEY")
        private val TRAVERSED_STEPS_KEY = TAG.plus("TRAVERSED_STEPS_KEY")
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
    private val brandSuggestionsChannel = Channel<List<Brand>>()

    val brandSuggestionsFlow = brandSuggestionsChannel.receiveAsFlow()

    private val attributeValueSuggestionsChannel = Channel<List<AttributeValue>>()

    val attributeValueSuggestionsFlow = attributeValueSuggestionsChannel.receiveAsFlow()

    private val attributeSuggestionsChannel = Channel<List<Attribute>>()

    val attributeSuggestionsFlow = attributeSuggestionsChannel.receiveAsFlow()

    private val storeSuggestionsChannel = Channel<List<Store>>()

    val storeSuggestionsFlow = storeSuggestionsChannel.receiveAsFlow()

    private val shopSuggestionsChannel = Channel<List<Shop>>()

    val shopSuggestionsFlow = shopSuggestionsChannel.receiveAsFlow()

    private val productSuggestionsChannel = Channel<List<Product>>()

    val productSuggestionsFlow = productSuggestionsChannel.receiveAsFlow()

    private val measurementUnitSuggestionsChannel = Channel<List<MeasurementUnit>>()

    val measurementUnitSuggestionsFlow = measurementUnitSuggestionsChannel.receiveAsFlow()

    private val saveProductStateChannel = Channel<State>()

    val saveProductStateFlow = saveProductStateChannel.receiveAsFlow()

    val traversedSteps = stateHandle.getStateFlow(
        TRAVERSED_STEPS_KEY,
        emptySet<AddProductStep>(),
    )

    fun saveCurrentlyActiveStep(step: AddProductStep) {
        stateHandle[CURRENT_ACTIVE_STEP_KEY] = step
        stateHandle[TRAVERSED_STEPS_KEY] = traversedSteps.value + step
    }

    fun savePermanently(stepsToPersist: Array<AddProductStep>) {
        viewModelScope.launch {
            saveProductStateChannel.send(State.Loading)

            productRepository.addProduct(product.value).also { result ->
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
                    stateHandle[TRAVERSED_STEPS_KEY] = emptySet<AddProductStep>()
                    saveProductStateChannel.send(State.Success(it))
                }.onFailure {
                    saveProductStateChannel.send(State.Failure(it))
                }
            }
        }
    }

    fun saveDraft(product: Product) {
        stateHandle[CURRENT_PRODUCT_KEY] = product.toLocalViewModel()
    }

    fun searchAttributeValue(attribute: AttributeValue) {
        viewModelScope.launch {
            attributeValueRepository.getAttributeValueSearchSuggestions(query = attribute)
                .onSuccess {
                    attributeValueSuggestionsChannel.send(listOf(attribute) + it)
                }.onFailure {
                    Log.e(TAG, "searchAttributeValue: ${it.localizedMessage}", it)
                    attributeValueSuggestionsChannel.send(listOf(attribute))
                }
        }
    }

    fun searchAttribute(attribute: Attribute) {
        viewModelScope.launch {
            attributeRepository.getAttributeSearchSuggestions(query = attribute)
                .onSuccess {
                    attributeSuggestionsChannel.send(listOf(attribute) + it)
                }.onFailure {
                    Log.e(TAG, "searchAttribute: ${it.localizedMessage}", it)
                    attributeSuggestionsChannel.send(listOf(attribute))
                }
        }
    }

    fun searchBrand(brand: Brand) {
        viewModelScope.launch {
            brandRepository.getBrandSearchSuggestions(query = brand)
                .onSuccess {
                    brandSuggestionsChannel.send(listOf(brand) + it)
                }.onFailure {
                    Log.e(TAG, "searchBrand: ${it.localizedMessage}", it)
                    brandSuggestionsChannel.send(listOf(brand))
                }
        }
    }

    fun searchStore(store: Store) {
        viewModelScope.launch {
            storeRepository.getStoreSearchSuggestions(query = store)
                .onSuccess {
                    storeSuggestionsChannel.send(listOf(store) + it)
                }.onFailure {
                    Log.e(TAG, "searchStore: ${it.localizedMessage}", it)
                    storeSuggestionsChannel.send(listOf(store))
                }
        }
    }

    fun searchShop(shop: Shop) {
        viewModelScope.launch {
            shopRepository.getShopSearchSuggestions(query = shop)
                .onSuccess {
                    shopSuggestionsChannel.send(listOf(shop) + it)
                }.onFailure {
                    Log.e(TAG, "searchShop: ${it.localizedMessage}", it)
                    shopSuggestionsChannel.send(listOf(shop))
                }
        }
    }

    fun searchProductName(name: ProductName) {
        viewModelScope.launch {
            val query = Product.LocalViewModel.default.copy(name = name.toLocalViewModel())
            productRepository.getProductSearchSuggestions(query = query)
                .onSuccess {
                    productSuggestionsChannel.send(listOf(query) + it)
                }.onFailure {
                    Log.e(TAG, "searchProductName: ${it.localizedMessage}", it)
                    productSuggestionsChannel.send(listOf(query))
                }
        }
    }

    fun searchMeasurementUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            measurementUnitRepository.getMeasurementUnitSearchSuggestions(query = unit)
                .onSuccess {
                    measurementUnitSuggestionsChannel.send(listOf(unit) + it)
                }.onFailure {
                    Log.e(TAG, "searchMeasurementUnit: ${it.localizedMessage}", it)
                    measurementUnitSuggestionsChannel.send(listOf(unit))
                }
        }
    }

    fun clearShopSearchSuggestions() {
        viewModelScope.launch {
            shopSuggestionsChannel.send(emptyList())
        }
    }

    fun clearStoreSearchSuggestions() {
        viewModelScope.launch {
            storeSuggestionsChannel.send(emptyList())
        }
    }

    fun clearProductSearchSuggestions() {
        viewModelScope.launch {
            productSuggestionsChannel.send(emptyList())
        }
    }

    fun clearMeasurementUnitSearchSuggestions() {
        viewModelScope.launch {
            measurementUnitSuggestionsChannel.send(emptyList())
        }
    }

    fun clearBrandSearchSuggestions() {
        viewModelScope.launch {
            brandSuggestionsChannel.send(emptyList())
        }
    }

    fun clearAttributeValueSuggestions() {
        viewModelScope.launch {
            attributeValueSuggestionsChannel.send(emptyList())
        }
    }

    fun clearAttributeSuggestions() {
        viewModelScope.launch {
            attributeSuggestionsChannel.send(emptyList())
        }
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
