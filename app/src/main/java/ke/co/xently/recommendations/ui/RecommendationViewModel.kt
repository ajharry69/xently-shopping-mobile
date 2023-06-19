package ke.co.xently.recommendations.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.recommendations.models.Recommendation
import ke.co.xently.recommendations.repositories.RecommendationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: RecommendationRepository,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private val TAG = RecommendationViewModel::class.java.simpleName
        private val REQUEST_KEY =
            RecommendationViewModel::class.java.simpleName.plus("REQUEST_KEY")
        private val SHOPPING_LIST_ITEM_KEY =
            RecommendationViewModel::class.java.simpleName.plus("SHOPPING_LIST_ITEM_KEY")
    }

    val recommendationRequest = stateHandle.getStateFlow(
        REQUEST_KEY,
        Recommendation.Request.default,
    )
    val draftShoppingListItem = stateHandle.getStateFlow(
        SHOPPING_LIST_ITEM_KEY,
        Recommendation.Request.ShoppingListItem.default,
    )

    fun saveDraftRecommendationRequest(request: Recommendation.Request) {
        stateHandle[REQUEST_KEY] = request
        Log.d(TAG, "saveDraftRecommendationRequest: $request")
    }

    fun saveDraftShoppingListItem(item: Recommendation.Request.ShoppingListItem) {
        stateHandle[SHOPPING_LIST_ITEM_KEY] = item
        Log.d(TAG, "saveDraftShoppingListItem: $item")
    }

    fun clearDraftShoppingListItem() {
        val item = stateHandle.remove<Recommendation.Request.ShoppingListItem>(SHOPPING_LIST_ITEM_KEY)
        Log.d(TAG, "clearDraftShoppingListItem: $item")
    }

    fun getRecommendations() {
        viewModelScope.launch {
            recommendationRequest.map(repository::getRecommendations)
                .collectLatest {
                    // TODO: replace with actual UI updatable states...
                    Log.d(TAG, "getRecommendations: $it")
                }
        }
    }
}