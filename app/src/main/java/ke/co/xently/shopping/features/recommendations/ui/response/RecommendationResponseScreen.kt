package ke.co.xently.shopping.features.recommendations.ui.response

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.OrderBy
import ke.co.xently.shopping.features.core.currencyNumberFormat
import ke.co.xently.shopping.features.core.toStringWithoutUnnecessaryDigitsAfterDecimalPoint
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import ke.co.xently.shopping.features.recommendations.ui.RecommendationResponseDetailsScreen
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortBy
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationResponseSortOptions
import ke.co.xently.shopping.features.recommendations.ui.components.RecommendationSummaryItemDropdownMenu
import ke.co.xently.shopping.features.recommendations.ui.components.StoreRecommendationSummaryItem
import kotlin.random.Random


@Composable
fun RecommendationResponseScreen(
    viewModel: RecommendationResponseViewModel = hiltViewModel(),
    navigateToStore: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
    onPaymentRequest: (serviceCharge: Number) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    RecommendationResponseScreen(
        modifier = Modifier,
        state = state,
        onNavigateBack = onNavigateBack,
        visitOnlineStore = visitOnlineStore,
        navigateToStore = navigateToStore,
        onPaymentRequest = onPaymentRequest,
        changeSortParameter = viewModel::changeSortParameter,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendationResponseScreen(
    modifier: Modifier,
    state: RecommendationResponseState,
    navigateToStore: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
    changeSortParameter: (SortParameter) -> Unit,
    onPaymentRequest: (serviceCharge: Number) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var showSortByDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var sortBy by rememberSaveable {
        mutableStateOf(RecommendationResponseSortBy.Default)
    }

    var orderBy by rememberSaveable {
        mutableStateOf(OrderBy.Ascending)
    }

    LaunchedEffect(sortBy, orderBy) {
        changeSortParameter(SortParameter(orderBy, sortBy))
    }

    AnimatedVisibility(visible = showSortByDialog) {
        AlertDialog(onDismissRequest = { showSortByDialog = false }) {
            RecommendationResponseSortOptions(sortBy) {
                sortBy = it
                showSortByDialog = false
            }
        }
    }

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(state) {
        if (state is RecommendationResponseState.Failure) {
            val message = state.error.localizedMessage
                ?: context.getString(R.string.xently_generic_error_message)

            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
            )
        }
    }

    var response: Recommendation.Response? by remember {
        mutableStateOf(null)
    }

    var openBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val onViewProduct: (Recommendation.Response) -> Unit = {
        response = it
        openBottomSheet = true
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.xently_recommendations))
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
        floatingActionButton = {
            if (state is RecommendationResponseState.Success && !state.data.isPaid) {
                ExtendedFloatingActionButton(
                    onClick = { onPaymentRequest(state.data.serviceCharge) },
                    text = {
                        Text(
                            text = stringResource(
                                R.string.xently_request_pay_service_charge,
                                context.currencyNumberFormat
                                    .format(state.data.serviceCharge)
                                    .toStringWithoutUnnecessaryDigitsAfterDecimalPoint(),
                            ).toUpperCase(Locale.current),
                        )
                    },
                    icon = {
                        Icon(Icons.Default.Payment, contentDescription = null)
                    },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Column {
                ListItem(
                    headlineContent = {
                        Surface(
                            onClick = { showSortByDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    Icons.Default.Sort,
                                    contentDescription = stringResource(R.string.xently_content_description_sort_recommendations),
                                )
                                Column {
                                    Text(text = stringResource(sortBy.label))
                                    PlainTooltipBox(
                                        tooltip = {
                                            Text(
                                                text = stringResource(
                                                    R.string.xently_switch_order,
                                                    stringResource(orderBy.opposite.label)
                                                        .toLowerCase(Locale.current),
                                                ),
                                            )
                                        },
                                    ) {
                                        Text(
                                            text = stringResource(orderBy.label),
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier
                                                .tooltipTrigger()
                                                .toggleable(
                                                    value = true,
                                                    role = Role.Switch,
                                                    onValueChange = {
                                                        orderBy = orderBy.opposite
                                                    },
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                    },
                )

                Divider()
            }

            LazyColumn(modifier = Modifier.then(modifier)) {
                if (state is RecommendationResponseState.Success) {
                    items(state.data.recommendations, key = { it.store.id }) { response ->
                        Surface(onClick = { onViewProduct(response) }) {
                            StoreRecommendationSummaryItem(response = response) {
                                var showComparisonListItemMenu by remember {
                                    mutableStateOf(false)
                                }
                                Box {
                                    val onClick by rememberUpdatedState {
                                        showComparisonListItemMenu = !showComparisonListItemMenu
                                    }
                                    AnimatedContent(
                                        showComparisonListItemMenu,
                                        label = "StoreAnimatedContent",
                                    ) {
                                        if (it) {
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = stringResource(
                                                    R.string.xently_content_description_options_for_item_reversed,
                                                    response.store,
                                                ),
                                                modifier = Modifier.clickable(onClick = onClick),
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.KeyboardArrowRight,
                                                contentDescription = stringResource(
                                                    R.string.xently_content_description_options_for_item,
                                                    response.store,
                                                ),
                                                modifier = Modifier.clickable(onClick = onClick),
                                            )
                                        }
                                    }
                                    RecommendationSummaryItemDropdownMenu(
                                        response = response,
                                        showComparisonListItemMenu = showComparisonListItemMenu,
                                        onNavigate = navigateToStore,
                                        onViewProduct = onViewProduct,
                                        visitOnlineStore = visitOnlineStore,
                                        onDismissRequest = {
                                            showComparisonListItemMenu = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState { sheetValue ->
        when (sheetValue) {
            SheetValue.Hidden -> true
            SheetValue.Expanded -> true
            SheetValue.PartiallyExpanded -> true
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                openBottomSheet = false
            },
        ) {
            RecommendationResponseDetailsScreen(
                modifier = Modifier,
                response = response!!,
                onNavigate = navigateToStore,
                visitOnlineStore = visitOnlineStore,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun RecommendationResponseScreenPreview() {
    XentlyTheme {
        val responseList = List(Random.nextInt(2, 10)) {
            Recommendation.Response.default.run {
                copy(
                    store = store.run {
                        copy(
                            id = (it + 1).toLong(),
                            name = "Store #".plus(Random.nextInt(1, 10)),
                            shop = shop.copy(name = "Shop #".plus(Random.nextInt(1, 10))),
                            distance = if ((it + 1) in arrayOf(1, 3, 7)) {
                                null
                            } else {
                                Random.nextDouble(50.0, 1000.0)
                            },
                        )
                    },
                    estimatedExpenditure = Recommendation.Response.EstimatedExpenditure.default.copy(
                        unit = Random.nextInt(1000, 5000).toDouble(),
                        total = Random.nextInt(1500, 10000).toDouble(),
                    ),
                    hit = Recommendation.Response.Hit.default.copy(count = Random.nextInt(5)),
                    miss = Recommendation.Response.Miss.default.copy(count = Random.nextInt(3)),
                )
            }
        }
        RecommendationResponseScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToStore = {},
            visitOnlineStore = {},
            onNavigateBack = {},
            changeSortParameter = {},
            onPaymentRequest = {},
            state = RecommendationResponseState.Success(
                data = RecommendationResponse.ViewModel(
                    requestId = -1,
                    recommendations = responseList,
                    serviceCharge = Random.nextDouble(50.0, 1000.0),
                ),
                sortParameter = SortParameter.default,
            ),
        )
    }
}