package ke.co.xently.shopping.features.authentication.ui.signup

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.authentication.models.SignUpRequest
import ke.co.xently.shopping.features.authentication.ui.components.PasswordTextField
import ke.co.xently.shopping.features.authentication.ui.components.RequiredEmailTextField
import ke.co.xently.shopping.features.core.loadingIndicatorLabel
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState(initial = SignUpState.Idle)
    SignUpScreen(
        state = state,
        signUp = viewModel::signUp,
        onSuccess = onSuccess,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScreen(
    state: SignUpState,
    signUp: (SignUpRequest) -> Unit,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val loading by remember(state) {
        derivedStateOf {
            state is SignUpState.Loading
        }
    }

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(state) {
        if (state is SignUpState.Success) {
            onSuccess()
        } else if (state is SignUpState.Failure) {
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
                    Text(text = stringResource(R.string.xently_page_title_sign_up))
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
                var firstName by remember {
                    mutableStateOf("")
                }
                NameTextField(
                    name = firstName,
                    label = stringResource(R.string.xently_text_field_label_first_name),
                ) {
                    firstName = it
                }

                var lastName by remember {
                    mutableStateOf("")
                }
                NameTextField(
                    name = lastName,
                    label = stringResource(R.string.xently_text_field_label_last_name),
                ) {
                    lastName = it
                }

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

                val focusManager = LocalFocusManager.current
                Button(
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        SignUpRequest(
                            firstName = firstName.takeIf { it.isNotBlank() },
                            lastName = lastName.takeIf { it.isNotBlank() },
                            email = email,
                            password = password,
                        ).let(signUp)
                        focusManager.clearFocus()
                    },
                ) {
                    Text(
                        text = loadingIndicatorLabel(
                            loading = loading,
                            label = stringResource(R.string.xently_page_title_sign_up)
                                .toUpperCase(Locale.current),
                            loadingLabelPrefix = stringResource(R.string.xently_button_label_sign_up_in_process),
                            keys = arrayOf(state),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun NameTextField(name: String, label: String, onValueChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words,
        ),
        singleLine = true,
        maxLines = 1,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SignUpScreenPreview() {
    XentlyTheme {
        SignUpScreen(
            state = SignUpState.Idle,
            signUp = {},
            onSuccess = {},
            onNavigateBack = {},
        )
    }
}