package ke.co.xently.remotedatasource.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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

    fun getSearchResults(): Flow<ResultState>

    suspend fun closeSession()

    open class Fake<in Q>(
        private val results: ResultState = ResultState.Idle,
    ) : AutoCompleteService<Q> {
        override suspend fun initSession(logSuccessfulInitialization: Boolean): InitState {
            return InitState.Idle
        }

        override fun getSearchResults(): Flow<ResultState> {
            return flowOf(results)
        }

        override suspend fun closeSession() {

        }

        override suspend fun search(query: Q, size: Int) {

        }
    }
}