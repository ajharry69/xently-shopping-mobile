package ke.co.xently.shopping.features.compareproducts.ui

import ke.co.xently.shopping.features.compareproducts.models.CompareProduct

sealed interface State {
    object Idle : State
    data class Success(val data: CompareProduct.Response) : State
    data class Failure(val error: Throwable) : State
    object Loading : State
}