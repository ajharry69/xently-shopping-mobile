package ke.co.xently.shopping.features.core.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.R

@Composable
fun MultiStepScreen(
    modifier: Modifier,
    @StringRes
    heading: Int,
    @StringRes
    subheading: Int? = null,
    scrollState: ScrollState? = rememberScrollState(),
    showBackButton: Boolean = true,
    enableBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    continueButton: @Composable BoxScope.() -> Unit,
    form: @Composable ColumnScope.() -> Unit,
) {
    MultiStepScreen(
        modifier = modifier,
        heading = stringResource(heading),
        subheading = subheading?.let { stringResource(it) },
        form = form,
        scrollState = scrollState,
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        enableBackButton = enableBackButton,
        continueButton = continueButton
    )
}

@Composable
fun MultiStepScreen(
    modifier: Modifier,
    heading: String,
    subheading: String? = null,
    scrollState: ScrollState? = rememberScrollState(),
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
                            text = heading,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    supportingContent = if (subheading == null) {
                        null
                    } else {
                        {
                            Text(text = subheading)
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
            modifier = Modifier
                .weight(1f)
                .then(
                    if (scrollState == null) {
                        Modifier
                    } else {
                        Modifier.verticalScroll(scrollState)
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )

        MultiStepNavigationButtons(
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