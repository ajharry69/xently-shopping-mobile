package ke.co.xently.features.compareproducts.ui

import ke.co.xently.features.compareproducts.models.CompareProduct

sealed interface State {
    object Idle : State
    data class Success(val data: List<CompareProduct.Response>) : State
    data class Failure(val error: Throwable) : State
    object Loading : State
}