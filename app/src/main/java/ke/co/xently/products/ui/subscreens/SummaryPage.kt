package ke.co.xently.products.ui.subscreens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.AddProductStep
import ke.co.xently.products.ui.State
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.products.ui.components.LabeledCheckbox
import ke.co.xently.ui.javaLocale
import ke.co.xently.ui.loadingIndicatorLabel
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

@Composable
fun SummaryPage(
    modifier: Modifier,
    product: Product,
    snackbarHostState: SnackbarHostState,
    stateState: StateFlow<State>,
    onPreviousClick: () -> Unit,
    onSubmissionSuccess: () -> Unit,
    submit: (Array<AddProductStep>) -> Unit,
) {
    val submissionState by stateState.collectAsState()

    val submitting by remember(submissionState) {
        derivedStateOf {
            submissionState is State.Loading
        }
    }

    val stepsToPersist = remember {
        mutableStateListOf<AddProductStep>()
    }

    val context = LocalContext.current

    LaunchedEffect(submissionState) {
        when (val state = submissionState) {
            is State.Failure -> {
                snackbarHostState.showSnackbar(
                    state.error.localizedMessage
                        ?: context.getString(R.string.xently_generic_error_message),
                    duration = SnackbarDuration.Long,
                )
            }

            State.Idle -> {

            }

            State.Loading -> {

            }

            is State.Success -> {
                onSubmissionSuccess()
            }
        }
    }

    AddProductPage(
        modifier = Modifier.then(modifier),
        heading = stringResource(R.string.xently_summary_page_title),
        subheading = stringResource(R.string.xently_summary_page_subtitle),
        onBackClick = onPreviousClick,
        enableBackButton = !submitting,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !submitting,
                onClick = {
                    submit(stepsToPersist.toTypedArray())
                },
            ) {
                Text(
                    text = loadingIndicatorLabel(
                        label = stringResource(R.string.xently_button_label_submit),
                        loading = submitting,
                        keys = arrayOf(submissionState),
                    ),
                )
            }
        },
    ) {
        Text(
            text = product.buildDescriptiveName(
                context = context,
                locale = Locale.current.javaLocale,
            ),
            fontWeight = FontWeight.Bold,
        )
        Text(text = stringResource(R.string.xently_reuse_details_intro))
        val (reuseStore, onReuseStoreChange) = remember {
            mutableStateOf(AddProductStep.Store in stepsToPersist)
        }
        val (reuseShop, onReuseShopChange) = remember {
            mutableStateOf(AddProductStep.Shop in stepsToPersist)
        }

        LaunchedEffect(reuseShop, reuseStore) {
            if (reuseShop) {
                stepsToPersist.add(AddProductStep.Shop)
            } else {
                stepsToPersist.removeIf { it == AddProductStep.Shop }
            }
            if (reuseStore) {
                stepsToPersist.add(AddProductStep.Store)
            } else {
                stepsToPersist.removeIf { it == AddProductStep.Store }
            }
        }

        ReuseStoreAndOrShop(
            reuseStore = reuseStore,
            reuseShop = reuseShop,
            onReuseStoreChange = onReuseStoreChange,
            onReuseShopChange = onReuseShopChange,
        )

    }
}


@Composable
private fun ReuseStoreAndOrShop(
    reuseStore: Boolean,
    reuseShop: Boolean,
    onReuseStoreChange: (Boolean) -> Unit,
    onReuseShopChange: (Boolean) -> Unit
) {
    LaunchedEffect(reuseStore, reuseShop) {
        if (reuseStore && !reuseShop) {
            // A store belongs to a shop, so if the user is going to reuse the
            // store, then they must reuse the shop as well
            onReuseShopChange(true)
        }
    }
    val parentState = remember(reuseStore, reuseShop) {
        if (reuseStore && reuseShop) {
            ToggleableState.On
        } else if (!reuseStore && !reuseShop) {
            ToggleableState.Off
        } else {
            ToggleableState.Indeterminate
        }
    }
    val onTriStateCheckboxClick by rememberUpdatedState {
        (parentState != ToggleableState.On).let {
            onReuseStoreChange(it)
            onReuseShopChange(it)
        }
    }
    Surface(onClick = onTriStateCheckboxClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TriStateCheckbox(
                state = parentState,
                onClick = onTriStateCheckboxClick,
            )
            Text(text = stringResource(R.string.xently_checkbox_label_reuse_store_and_shop))
        }
    }
    Column(
        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LabeledCheckbox(
            checked = reuseStore,
            onCheckedChange = onReuseStoreChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.xently_checkbox_label_reuse_store),
        )
        LabeledCheckbox(
            checked = reuseShop,
            // A store belongs to a shop, so if the user is going to reuse the
            // store, then they must reuse the shop as well
            enabled = !reuseStore,
            onCheckedChange = onReuseShopChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.xently_checkbox_label_reuse_shop),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SummaryPage() {
    XentlyTheme {
        SummaryPage(
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default.run {
                copy(
                    name = name.copy(name = "Bread"),
                    store = store.run {
                        copy(
                            name = "Riruta",
                            shop = shop.copy(name = "Naivas"),
                        )
                    },
                    measurementUnit = MeasurementUnit.LocalViewModel.default.copy(
                        name = "Kilogram",
                    ),
                    unitPrice = BigDecimal("420"),
                    measurementUnitQuantity = 2f,
                    brands = listOf(
                        Brand.LocalViewModel.default.copy(name = "Kabras"),
                        Brand.LocalViewModel.default.copy(name = "Techpak"),
                    ),
                    attributes = listOf(AttributeValue.LocalViewModel.default.run {
                        copy(
                            value = "Brown",
                            attribute = attribute.copy(name = "Kind")
                        )
                    }),
                )
            },
            stateState = MutableStateFlow(State.Idle),
            snackbarHostState = SnackbarHostState(),
            onPreviousClick = {},
            onSubmissionSuccess = {},
            submit = {},
        )
    }
}