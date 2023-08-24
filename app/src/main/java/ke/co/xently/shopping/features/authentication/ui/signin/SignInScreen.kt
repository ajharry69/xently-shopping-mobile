package ke.co.xently.shopping.features.authentication.ui.signin

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.authentication.models.SignInRequest
import ke.co.xently.shopping.features.authentication.ui.components.PasswordTextField
import ke.co.xently.shopping.features.authentication.ui.components.RequiredEmailTextField
import ke.co.xently.shopping.features.core.loadingIndicatorLabel
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    requestRegistration: () -> Unit,
    forgetPassword: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState(initial = SignInState.Idle)
    SignInScreen(
        state = state,
        signIn = viewModel::signIn,
        onSuccess = onSuccess,
        onNavigateBack = onNavigateBack,
        requestRegistration = requestRegistration,
        forgotPassword = forgetPassword,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignInScreen(
    state: SignInState,
    signIn: (SignInRequest) -> Unit,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    requestRegistration: () -> Unit,
    forgotPassword: () -> Unit,
) {
    val loading by remember(state) {
        derivedStateOf {
            state is SignInState.Loading
        }
    }

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(state) {
        if (state is SignInState.Success) {
            onSuccess()
        } else if (state is SignInState.Failure) {
            val message = state.error.localizedMessage
                ?: context.getString(R.string.xently_generic_error_message)

            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.xently_page_title_sign_in))
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
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                var email by remember {
                    mutableStateOf("")
                }
                RequiredEmailTextField(email) {
                    email = it
                }

                var password by remember {
                    mutableStateOf("")
                }
                PasswordTextField(
                    password = password,
                    label = stringResource(R.string.xently_text_field_label_password_required),
                    onValueChange = { password = it },
                )

                TextButton(onClick = forgotPassword, modifier = Modifier.align(Alignment.End)) {
                    Text(text = stringResource(R.string.xently_button_label_forgot_password))
                }

                Button(
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        SignInRequest(email = email, password = password)
                            .let(signIn)
                    },
                ) {
                    Text(
                        text = loadingIndicatorLabel(
                            loading = loading,
                            label = stringResource(R.string.xently_page_title_sign_in)
                                .toUpperCase(Locale.current),
                            loadingLabelPrefix = stringResource(R.string.xently_button_label_sign_in_in_process),
                            keys = arrayOf(state),
                        ),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.xently_or),
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Divider(modifier = Modifier.weight(1f))
                }

                TextButton(onClick = requestRegistration, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.xently_button_label_sign_in_register_new_account))
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SignInScreenPreview() {
    XentlyTheme {
        SignInScreen(
            state = SignInState.Idle,
            signIn = {},
            onSuccess = {},
            onNavigateBack = {},
            requestRegistration = {},
            forgotPassword = {},
        )
    }
}