package ke.co.xently.shopping.features.authentication.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme

@Composable
fun PasswordTextField(
    password: String,
    label: String,
    imeAction: ImeAction = ImeAction.Done,
    onValueChange: (String) -> Unit,
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    TextField(
        value = password,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password,
        ),
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                if (isPasswordVisible) {
                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.xently_content_description_hide_password),
                    )
                } else {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = stringResource(R.string.xently_content_description_show_password),
                    )
                }
            }
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun PasswordTextFieldPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        var password by remember {
            mutableStateOf("")
        }
        PasswordTextField(
            password = password,
            label = "Password",
            onValueChange = { password = it },
        )
    }
}