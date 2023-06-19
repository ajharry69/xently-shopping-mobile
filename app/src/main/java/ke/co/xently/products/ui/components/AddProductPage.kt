package ke.co.xently.products.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.co.xently.R

@Composable
fun AddProductPage(
    modifier: Modifier = Modifier,
    @StringRes
    heading: Int,
    @StringRes
    subHeading: Int? = null,
    showBackButton: Boolean = true,
    enableBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    continueButton: @Composable BoxScope.() -> Unit,
    form: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        Column(
            content = {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(heading),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    supportingContent = if (subHeading == null) {
                        null
                    } else {
                        {
                            Text(text = stringResource(subHeading))
                        }
                    },
                )
                Column(
                    content = form,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                )
            },
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )

        AddProductNavigationButtons(
            buttons = {
                AnimatedVisibility(visible = showBackButton, modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onBackClick,
                        enabled = enableBackButton,
                    ) {
                        Text(stringResource(R.string.xently_button_label_back))
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    continueButton()
                }
            },
            modifier = Modifier.wrapContentHeight(),
        )
    }
}