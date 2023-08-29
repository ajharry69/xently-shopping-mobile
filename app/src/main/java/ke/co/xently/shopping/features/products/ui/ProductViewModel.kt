package ke.co.xently.shopping.features.products.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.products.models.Product
import ke.co.xently.shopping.features.products.repositories.ProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
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

    val flowOfSaveProductState = saveProductStateChannel.receiveAsFlow()

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

                            AddProductStep.MeasurementUnitName -> {
                                newDraftProduct = newDraftProduct.copy(
                                    measurementUnit = it.measurementUnit,
                                )
                            }

                            AddProductStep.MeasurementUnitQuantity -> {
                                newDraftProduct = newDraftProduct.copy(
                                    measurementUnitQuantity = it.measurementUnitQuantity,
                                )
                            }

                            AddProductStep.Brands -> {
                                newDraftProduct = newDraftProduct.copy(brands = it.brands)
                            }

                            AddProductStep.Attributes -> {
                                newDraftProduct =
                                    newDraftProduct.copy(attributeValues = it.attributeValues)
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
