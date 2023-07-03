package ke.co.xently.features.compareproducts.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.compareproducts.models.CompareProduct
import ke.co.xently.features.compareproducts.models.ComparisonListItem
import ke.co.xently.features.compareproducts.repositories.CompareProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareProductViewModel @Inject constructor(
    private val repository: CompareProductRepository,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val DEFAULT_COMPARISON_LIST_ITEM_INDEX = -1
        private val TAG = CompareProductViewModel::class.java.simpleName
        private val REQUEST_KEY = TAG.plus("REQUEST_KEY")
        private val COMPARISON_LIST_ITEM_KEY = TAG.plus("COMPARISON_LIST_ITEM_KEY")
        private val COMPARISON_LIST_ITEM_INDEX_KEY = TAG.plus("COMPARISON_LIST_ITEM_INDEX_KEY")
    }

    private val compareProductsStateChannel = Channel<State>()
    val comparisonsStateFlow = compareProductsStateChannel.receiveAsFlow()
    val comparisonRequest = stateHandle.getStateFlow(
        REQUEST_KEY,
        CompareProduct.Request.default,
    )
    val draftComparisonListItem = stateHandle.getStateFlow(
        COMPARISON_LIST_ITEM_KEY,
        ComparisonListItem.default,
    )
    val draftComparisonListItemIndex = stateHandle.getStateFlow(
        COMPARISON_LIST_ITEM_INDEX_KEY,
        DEFAULT_COMPARISON_LIST_ITEM_INDEX,
    )

    fun saveDraftCompareProductsRequest(request: CompareProduct.Request) {
        stateHandle[REQUEST_KEY] = request
    }

    fun saveDraftComparisonListItem(
        item: ComparisonListItem,
        index: Int = DEFAULT_COMPARISON_LIST_ITEM_INDEX,
    ) {
        stateHandle[COMPARISON_LIST_ITEM_KEY] = item
        stateHandle[COMPARISON_LIST_ITEM_INDEX_KEY] = index
    }

    fun clearDraftComparisonListItem() {
        stateHandle[COMPARISON_LIST_ITEM_KEY] = ComparisonListItem.default
        stateHandle[COMPARISON_LIST_ITEM_INDEX_KEY] = DEFAULT_COMPARISON_LIST_ITEM_INDEX
    }

    fun compareProducts() {
        viewModelScope.launch {
            compareProductsStateChannel.send(State.Loading)

            repository.compareProducts(comparisonRequest.value).also { result ->
                result.onSuccess {
                    compareProductsStateChannel.send(State.Success(it))
                }.onFailure {
                    compareProductsStateChannel.send(State.Failure(it))
                }
            }
        }
    }
}