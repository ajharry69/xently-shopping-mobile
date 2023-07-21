package ke.co.xently.features.products.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.attributes.datasources.remoteservices.AttributeAutoCompleteService
import ke.co.xently.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService
import ke.co.xently.features.brands.datasources.remoteservices.BrandAutoCompleteService
import ke.co.xently.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService
import ke.co.xently.features.products.datasources.remoteservices.ProductAutoCompleteService
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.repositories.ProductRepository
import ke.co.xently.features.shop.datasources.remoteservices.ShopAutoCompleteService
import ke.co.xently.features.store.datasources.remoteservices.StoreAutoCompleteService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,

    val shopAutoCompleteService: ShopAutoCompleteService,
    val storeAutoCompleteService: StoreAutoCompleteService,
    val brandAutoCompleteService: BrandAutoCompleteService,
    val productAutoCompleteService: ProductAutoCompleteService,
    val attributeAutoCompleteService: AttributeAutoCompleteService,
    val attributeValueAutoCompleteService: AttributeValueAutoCompleteService,
    val measurementUnitAutoCompleteService: MeasurementUnitAutoCompleteService,
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
}
