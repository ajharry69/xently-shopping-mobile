package ke.co.xently.shopping.features.collectpayments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import ke.co.xently.shopping.features.collectpayments.repositories.MpesaPaymentRepository
import ke.co.xently.shopping.features.recommendations.repositories.RecommendationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MpesaPaymentViewModel @Inject constructor(
    private val repository: MpesaPaymentRepository,
    private val recommendationRepository: RecommendationRepository,
) : ViewModel() {
    private val stateChannel = Channel<MpesaPaymentRequestState>()
    val stateFlow = stateChannel.receiveAsFlow()

    fun pay(request: MpesaPaymentRequest) {
        viewModelScope.launch {
            stateChannel.send(MpesaPaymentRequestState.Loading)

            repository.pay(request).also { result ->
                result.onSuccess {
                    stateChannel.send(MpesaPaymentRequestState.ConfirmingPayment)

                    var error: Throwable? = null
                    var retryCount = 1
                    do {
                        recommendationRepository.getDecryptionCredentials().also {
                            it.onSuccess {
                                stateChannel.send(MpesaPaymentRequestState.Success)
                            }.onFailure { throwable ->
                                error = throwable
                                retryCount += 1

                                val retryDuration = (retryCount * 10).seconds
                                Timber.tag(TAG).i(
                                    "Retrying getting decryption credentials in %s",
                                    retryDuration,
                                )
                                delay(retryDuration)
                            }
                        }
                    } while (retryCount < 6)

                    error?.let {
                        Timber.tag(TAG).e(it, "pay: %s", it.localizedMessage)
                        stateChannel.send(MpesaPaymentRequestState.Failure(it))
                    }
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
