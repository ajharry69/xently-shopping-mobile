package ke.co.xently.remotedatasource.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AutoCompleteService<in Q, out R> {

    suspend fun initSession(): Result<Unit>

    suspend fun search(query: Q, size: Int = 5)

    fun getSearchResults(): Flow<List<R>>

    suspend fun closeSession()

    open class Fake<in Q, out R>(
        private val results: List<R> = emptyList(),
    ) : AutoCompleteService<Q, R> {
        override suspend fun initSession(): Result<Unit> {
            return Result.success(Unit)
        }

        override fun getSearchResults(): Flow<List<R>> {
            return flowOf(results)
        }

        override suspend fun closeSession() {

        }

        override suspend fun search(query: Q, size: Int) {

        }
    }
}