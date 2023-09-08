package ke.co.xently.shopping.features.collectpayments.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.collectpayments.models.MpesaPaymentRequest
import ke.co.xently.shopping.features.core.cleansedForNumberParsing
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.loadingIndicatorLabel
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import java.math.BigDecimal


@Composable
fun MpesaPaymentRequestScreen(
    viewModel: MpesaPaymentViewModel = hiltViewModel(),
    serviceCharge: BigDecimal,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState(initial = MpesaPaymentRequestState.Idle)
    MpesaPaymentRequestScreen(
        state = state,
        serviceCharge = serviceCharge,
        pay = viewModel::pay,
        onSuccess = onSuccess,
        onNavigateBack = onNavigateBack,
        confirmPayment = viewModel::confirmPayment,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MpesaPaymentRequestScreen(
    state: MpesaPaymentRequestState,
    serviceCharge: BigDecimal,
    pay: (MpesaPaymentRequest) -> Unit,
    confirmPayment: () -> Unit,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val loading by remember(state) {
        derivedStateOf {
            state is MpesaPaymentRequestState.Loading
        }
    }

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(state) {
        if (state is MpesaPaymentRequestState.Success) {
            onSuccess()
        } else if (state is MpesaPaymentRequestState.Failure) {
            val message = state.error.localizedMessage
                ?: context.getString(R.string.xently_generic_error_message)

            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.xently_page_title_checkout))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.xently_content_description_navigate_back_icon),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.xently_page_sub_title_pay_with_mpesa),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Text(text = stringResource(R.string.xently_checkout_mpesa_description))

                    var phoneNumber by rememberSaveable {
                        mutableStateOf("")
                    }

                    TextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                        label = {
                            Text(text = stringResource(R.string.xently_text_field_label_phone_number_required))
                        },
                        prefix = {
                            Text(text = "+254")
                        },
                    )

                    val focusManager = LocalFocusManager.current
                    Button(
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val cleansedPhoneNumber = phoneNumber.cleansedForNumberParsing()
                                .removePrefix("+")
                                .removePrefix("254")
                            MpesaPaymentRequest(phoneNumber = "254$cleansedPhoneNumber".toLong())
                                .let(pay)
                            focusManager.clearFocus()
                        },
                    ) {
                        Text(
                            text = loadingIndicatorLabel(
                                loading = loading,
                                label = stringResource(
                                    R.string.xently_pay_service_charge,
                                    context.currencyNumberFormat
                                        .format(serviceCharge)
                                        .removeSuffix(".00"),
                                ).toUpperCase(Locale.current),
                                loadingLabelPrefix = stringResource(R.string.xently_button_label_payment_in_progress),
                                keys = arrayOf(state),
                            ),
                        )
                    }
                }

                OutlinedButton(
                    enabled = state !is MpesaPaymentRequestState.ConfirmingPayment,
                    onClick = confirmPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ) {
                    Text(
                        text = loadingIndicatorLabel(
                            label = stringResource(R.string.xently_confirm_payment)
                                .toUpperCase(Locale.current),
                            loading = state is MpesaPaymentRequestState.ConfirmingPayment,
                            loadingLabelPrefix = stringResource(R.string.xently_button_label_confirming_payment),
                        ),
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun MpesaPaymentRequestScreenPreview() {
    XentlyTheme {
        MpesaPaymentRequestScreen(
            state = MpesaPaymentRequestState.Idle,
            serviceCharge = BigDecimal("50000"),
            pay = {},
            onSuccess = {},
            confirmPayment = {},
            onNavigateBack = {},
        )
    }
}