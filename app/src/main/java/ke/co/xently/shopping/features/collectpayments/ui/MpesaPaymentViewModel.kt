package ke.co.xently.shopping.features.collectpayments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import ke.co.xently.shopping.features.collectpayments.repositories.MpesaPaymentRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MpesaPaymentViewModel @Inject constructor(
    private val repository: MpesaPaymentRepository,
) : ViewModel() {
    private val stateChannel = Channel<MpesaPaymentRequestState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun pay(request: MpesaPaymentRequest) {
        viewModelScope.launch {
            stateChannel.send(MpesaPaymentRequestState.Loading)

            repository.pay(request).also { result ->
                result.onSuccess {
                    stateChannel.send(MpesaPaymentRequestState.Success)
                }.onFailure {
                    Timber.tag(TAG).e(it, "pay: %s", it.localizedMessage)
                    stateChannel.send(MpesaPaymentRequestState.Failure(it))
                }
            }
        }
    }

    companion object {
        private val TAG: String = MpesaPaymentViewModel::class.java.simpleName
    }
}
