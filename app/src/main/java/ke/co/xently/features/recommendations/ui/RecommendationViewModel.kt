package ke.co.xently.features.recommendations.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.features.core.models.Location
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.repositories.RecommendationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: RecommendationRepository,
) : ViewModel() {
    companion object {
        const val DEFAULT_SHOPPING_LIST_ITEM_INDEX = -1
        private val TAG = RecommendationViewModel::class.java.simpleName
        private val REQUEST_KEY = TAG.plus("REQUEST_KEY")
        private val SHOPPING_LIST_ITEM_KEY = TAG.plus("SHOPPING_LIST_ITEM_KEY")
        private val SHOPPING_LIST_ITEM_INDEX_KEY = TAG.plus("SHOPPING_LIST_ITEM_INDEX_KEY")
    }

    private val recommendationsStateChannel = Channel<State>()
    val recommendationsStateFlow = recommendationsStateChannel.receiveAsFlow()
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

    fun getRecommendations(location: Location) {
        viewModelScope.launch {
            recommendationsStateChannel.send(State.Loading)

            repository.getRecommendations(recommendationRequest.value.copy(currentLocation = location))
                .also { result ->
                    result.onSuccess {
                        recommendationsStateChannel.send(State.Success(it))
                    }.onFailure {
                        recommendationsStateChannel.send(State.Failure(it))
                    }
                }
        }
    }

    fun flagGettingCurrentLocation() {
        viewModelScope.launch {
            recommendationsStateChannel.send(State.GettingCurrentLocation)
        }
    }
}