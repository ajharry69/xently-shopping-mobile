package ke.co.xently.shopping.features.authentication.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.tooling.preview.Preview
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme

@Composable
fun RequiredEmailTextField(
    email: String,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = email,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(R.string.xently_text_field_label_email_required))
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email,
        ),
        singleLine = true,
        maxLines = 1,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RequiredEmailTextFieldPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        var email by remember {
            mutableStateOf("")
        }
        RequiredEmailTextField(email) {
            email = it
        }
    }
}