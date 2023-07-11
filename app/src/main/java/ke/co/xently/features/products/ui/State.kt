package ke.co.xently.features.products.ui

import ke.co.xently.features.products.models.Product

sealed interface State {
    object Idle : State
    data class Success(val data: Product.LocalViewModel) : State
    data class Failure(val error: Throwable) : State
    object Loading : State
}