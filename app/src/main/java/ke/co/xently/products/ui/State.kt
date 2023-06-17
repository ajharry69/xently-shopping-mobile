package ke.co.xently.products.ui

import ke.co.xently.products.models.Product

sealed interface State {
    object Idle : State
    data class Success(private val data: Product.LocalViewModel) : State
    data class Failure(private val error: Throwable) : State
    object Loading : State
}