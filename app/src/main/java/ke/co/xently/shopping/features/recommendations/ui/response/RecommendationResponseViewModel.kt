package ke.co.xently.shopping.features.recommendations.ui.response

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.repositories.RecommendationRepository
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortBy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecommendationResponseViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    repository: RecommendationRepository,
) : ViewModel() {
    fun changeSortParameter(sortParameter: SortParameter) {
        stateHandle[SORT_PARAMETER_KEY] = sortParameter
    }

    private val sortParameterFlow =
        stateHandle.getStateFlow(SORT_PARAMETER_KEY, SortParameter.default)

    private val recommendationResponseFlow = repository.getLatestRecommendations().mapLatest {
        if (it == null) {
            RecommendationResponseState.Idle
        } else {
            RecommendationResponseState.Success(it, SortParameter.default)
        }
    }.onStart {
        emit(RecommendationResponseState.Loading)
    }.catch {
        Timber.tag(TAG).e(it, "getLatestRecommendations: %s", it.localizedMessage)
        emit(RecommendationResponseState.Failure(it))
    }

    val stateFlow =
        sortParameterFlow.combineTransform(recommendationResponseFlow) { sortParameter, responseState ->
            if (responseState !is RecommendationResponseState.Success) {
                emit(responseState)
            } else {
                val sortSelector: (Recommendation.Response) -> String? = {
                    when (sortParameter.sortBy) {
                        RecommendationResponseSortBy.Default -> null
                        RecommendationResponseSortBy.StoreName -> it.store.name
                        RecommendationResponseSortBy.StoreDistance -> it.store.distance?.toString()
                        RecommendationResponseSortBy.HitCount -> it.hit.count.toString()
                        RecommendationResponseSortBy.MissCount -> it.miss.count.toString()
                        RecommendationResponseSortBy.EstimatedExpenditure -> it.estimatedExpenditure.total.toString()
                    }
                }

                val recommendations = when (sortParameter.orderBy) {
                    OrderBy.Ascending -> {
                        responseState.data.recommendations.sortedBy(sortSelector)
                    }

                    OrderBy.Descending -> {
                        responseState.data.recommendations.sortedByDescending(sortSelector)
                    }
                }
                val success = responseState.copy(
                    sortParameter = sortParameter,
                    data = responseState.data.copy(recommendations = recommendations),
                )
                emit(success)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            RecommendationResponseState.Idle,
        )

    companion object {
        private val TAG: String = RecommendationResponseViewModel::class.java.simpleName
        private val SORT_PARAMETER_KEY = TAG.plus("SORT_PARAMETER_KEY")
    }
}