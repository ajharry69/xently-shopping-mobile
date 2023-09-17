package ke.co.xently.shopping.datasource.remote.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

interface AutoCompleteService<in Q> {
    sealed interface InitState {
        object Idle : InitState
        object Loading : InitState

        object Success : InitState

        data class Failure(val error: Throwable) : InitState
    }

    sealed interface ResultState {
        object Idle : ResultState
        object Loading : ResultState

        data class Success<out T>(val data: List<T>) : ResultState

        data class Failure(val error: Throwable) : ResultState
    }

    suspend fun initSession(logSuccessfulInitialization: Boolean = true): InitState

    suspend fun search(query: Q, size: Int = 5)

    val resultState: SharedFlow<ResultState>

    suspend fun initGetSearchResults()

    suspend fun closeSession()

    open class Fake<in Q>(results: ResultState = ResultState.Idle) : AutoCompleteService<Q> {
        override val resultState: SharedFlow<ResultState> = MutableStateFlow(results)

        override suspend fun initSession(logSuccessfulInitialization: Boolean): InitState {
            return InitState.Idle
        }

        override suspend fun initGetSearchResults() {
        }

        override suspend fun closeSession() {

        }

        override suspend fun search(query: Q, size: Int) {

        }
    }
}