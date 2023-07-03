package ke.co.xently.features.recommendations.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.repositories.RecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: RecommendationRepository,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val DEFAULT_SHOPPING_LIST_ITEM_INDEX = -1
        private val TAG = RecommendationViewModel::class.java.simpleName
        private val REQUEST_KEY = TAG.plus("REQUEST_KEY")
        private val SHOPPING_LIST_ITEM_KEY = TAG.plus("SHOPPING_LIST_ITEM_KEY")
        private val SHOPPING_LIST_ITEM_INDEX_KEY = TAG.plus("SHOPPING_LIST_ITEM_INDEX_KEY")
    }

    private val mutableRecommendationsState = MutableStateFlow<State>(State.Idle)
    val recommendationsState = mutableRecommendationsState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = State.Idle,
    )
    val recommendationRequest = stateHandle.getStateFlow(
        REQUEST_KEY,
        Recommendation.Request.default,
    )
    val draftShoppingListItem = stateHandle.getStateFlow(
        SHOPPING_LIST_ITEM_KEY,
        Recommendation.Request.ShoppingListItem.default,
    )
    val draftShoppingListItemIndex = stateHandle.getStateFlow(
        SHOPPING_LIST_ITEM_INDEX_KEY,
        DEFAULT_SHOPPING_LIST_ITEM_INDEX,
    )

    fun saveDraftRecommendationRequest(request: Recommendation.Request) {
        stateHandle[REQUEST_KEY] = request
    }

    fun saveDraftShoppingListItem(
        item: Recommendation.Request.ShoppingListItem,
        index: Int = DEFAULT_SHOPPING_LIST_ITEM_INDEX,
    ) {
        stateHandle[SHOPPING_LIST_ITEM_KEY] = item
        stateHandle[SHOPPING_LIST_ITEM_INDEX_KEY] = index
    }

    fun clearDraftShoppingListItem() {
        stateHandle[SHOPPING_LIST_ITEM_KEY] = Recommendation.Request.ShoppingListItem.default
        stateHandle[SHOPPING_LIST_ITEM_INDEX_KEY] = DEFAULT_SHOPPING_LIST_ITEM_INDEX
    }

    fun getRecommendations() {
        viewModelScope.launch {
            recommendationRequest.onStart {
                mutableRecommendationsState.value = State.Loading
            }.map(repository::getRecommendations)
                .collectLatest { result ->
                    result.onSuccess {
                        mutableRecommendationsState.value = State.Success(it)
                    }.onFailure {
                        mutableRecommendationsState.value = State.Failure(it)
                    }
                }
        }
    }
}