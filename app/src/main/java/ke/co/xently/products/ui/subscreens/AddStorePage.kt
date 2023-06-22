package ke.co.xently.products.ui.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.google.maps.android.compose.GoogleMap
import ke.co.xently.R
import ke.co.xently.products.models.Store
import ke.co.xently.products.ui.components.AddProductPage

@Composable
fun AddStorePage(
    modifier: Modifier = Modifier,
    store: Store,
    onContinueClick: (Store) -> Unit,
) {
    var name by remember(store.name) { mutableStateOf(TextFieldValue(store.name)) }
    var showMap by remember {
        mutableStateOf(false)
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_add_store_page_title,
        subHeading = R.string.xently_add_store_page_sub_heading,
        showBackButton = false,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    store.toLocalViewModel().copy(
                        name = name.text,
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_name))
            },
            supportingText = {
                Text(text = stringResource(R.string.xently_text_field_help_text_store))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                onMapLoaded = {
                    showMap = true
                },
            )
            if (!showMap) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}