package ke.co.xently.features.compareproducts.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.compareproducts.models.CompareProduct
import ke.co.xently.features.compareproducts.repositories.CompareProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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

    private val mutableCompareProductsState = MutableStateFlow<State>(State.Idle)
    val comparisonsState = mutableCompareProductsState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = State.Idle,
    )
    val comparisonRequest = stateHandle.getStateFlow(
        REQUEST_KEY,
        CompareProduct.Request.default,
    )
    val draftComparisonListItem = stateHandle.getStateFlow(
        COMPARISON_LIST_ITEM_KEY,
        CompareProduct.Request.ComparisonListItem.default,
    )
    val draftComparisonListItemIndex = stateHandle.getStateFlow(
        COMPARISON_LIST_ITEM_INDEX_KEY,
        DEFAULT_COMPARISON_LIST_ITEM_INDEX,
    )

    fun saveDraftCompareProductsRequest(request: CompareProduct.Request) {
        stateHandle[REQUEST_KEY] = request
    }

    fun saveDraftComparisonListItem(
        item: CompareProduct.Request.ComparisonListItem,
        index: Int = DEFAULT_COMPARISON_LIST_ITEM_INDEX,
    ) {
        stateHandle[COMPARISON_LIST_ITEM_KEY] = item
        stateHandle[COMPARISON_LIST_ITEM_INDEX_KEY] = index
    }

    fun clearDraftComparisonListItem() {
        stateHandle[COMPARISON_LIST_ITEM_KEY] = CompareProduct.Request.ComparisonListItem.default
        stateHandle[COMPARISON_LIST_ITEM_INDEX_KEY] = DEFAULT_COMPARISON_LIST_ITEM_INDEX
    }

    fun getCompareProducts() {
        viewModelScope.launch {
            comparisonRequest.onStart {
                mutableCompareProductsState.value = State.Loading
            }.map(repository::getCompareProducts)
                .collectLatest { result ->
                    result.onSuccess {
                        mutableCompareProductsState.value = State.Success(it)
                    }.onFailure {
                        mutableCompareProductsState.value = State.Failure(it)
                    }
                }
        }
    }
}