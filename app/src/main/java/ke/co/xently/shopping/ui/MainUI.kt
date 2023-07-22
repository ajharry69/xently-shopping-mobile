package ke.co.xently.shopping.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import ke.co.xently.shopping.BottomSheet
import ke.co.xently.shopping.HomeTab
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.MainViewModel
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.compareproducts.repositories.CompareProductRepository
import ke.co.xently.shopping.features.compareproducts.ui.CompareProductViewModel
import ke.co.xently.shopping.features.compareproducts.ui.CompareProductsRequestScreen
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.core.visitUriPage
import ke.co.xently.shopping.features.products.repositories.ProductRepository
import ke.co.xently.shopping.features.products.ui.AddProductScreen
import ke.co.xently.shopping.features.products.ui.ProductViewModel
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import ke.co.xently.shopping.features.recommendations.repositories.RecommendationRepository
import ke.co.xently.shopping.features.recommendations.ui.RecommendationRequestScreen
import ke.co.xently.shopping.features.recommendations.ui.RecommendationViewModel
import ke.co.xently.shopping.ui.components.ModalBottomSheet
import kotlinx.coroutines.launch

private const val TAG = "MainUI"

@Composable
fun MainUI() {
    val viewModel = hiltViewModel<MainViewModel>()
    val selectedTab by viewModel.currentlyActiveTab.collectAsState()

    val scope = rememberCoroutineScope()

    val snackbarHostState = LocalSnackbarHostState.current

    val context = LocalContext.current
    val navigateToStore: (Recommendation.Response) -> Unit by rememberUpdatedState { recommendation ->
        val navigationQuery = recommendation.store.run {
            location.let {
                "${it.latitude},${it.longitude}"
            }
        }
        val googleMapsPackageName = "com.google.android.apps.maps"
        val uri = Uri.parse("google.navigation:q=$navigationQuery")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        if (mapIntent.resolveActivity(context.packageManager) == null) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    context.getString(R.string.xently_error_navigation_app_not_found),
                    actionLabel = context.getString(R.string.xently_install)
                        .toUpperCase(Locale.current),
                    duration = SnackbarDuration.Long,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    context.visitUriPage(
                        "market://details?id=$googleMapsPackageName",
                        logTag = TAG,
                        onActivityNotFound = {
                            context.visitUriPage(
                                "https://play.google.com/store/apps/details?id=$googleMapsPackageName",
                                logTag = TAG,
                            )
                        },
                    )
                }
            }
        } else {
            mapIntent.run {
                setPackage(googleMapsPackageName)
                if (resolveActivity(context.packageManager) != null) {
                    // Prefer Google Maps over other apps
                    context.startActivity(this)
                } else {
                    context.startActivity(mapIntent)
                }
            }
        }
    }

    val stackOfBottomSheets = remember {
        mutableStateListOf<BottomSheet>()
    }

    MainUI(
        selectedTab = selectedTab,
        bottomSheet = { stackOfBottomSheets.firstOrNull() ?: BottomSheet.Ignore },
        hideBottomSheet = {
            stackOfBottomSheets.removeFirstOrNull() == null
        },
        onTabClicked = viewModel::saveCurrentlyActiveTab,
        navigateToStore = navigateToStore,
        visitOnlineStore = { response ->
            response.store.shop.ecommerceSiteUrl.takeIf { !it.isNullOrBlank() }?.also {
                context.visitUriPage(it.trim(), logTag = TAG)
            }
        },
        updateBottomSheetPeek = {
            stackOfBottomSheets.add(0, it)
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainUI(
    selectedTab: HomeTab,
    bottomSheet: () -> BottomSheet,
    hideBottomSheet: () -> Boolean,
    onTabClicked: (HomeTab) -> Unit,
    navigateToStore: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
    updateBottomSheetPeek: (BottomSheet) -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    recommendationViewModel: RecommendationViewModel = hiltViewModel(),
    compareProductViewModel: CompareProductViewModel = hiltViewModel(),
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = LocalSnackbarHostState.current)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                for (tab in HomeTab.values()) {
                    val title = stringResource(tab.title)
                    PlainTooltipBox(
                        tooltip = {
                            Text(text = title)
                        },
                    ) {
                        Tab(
                            modifier = Modifier.tooltipTrigger(),
                            selected = selectedTab == tab,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {
                                onTabClicked(tab)
                            },
                            icon = {
                                Icon(tab.image, contentDescription = title)
                            },
                            text = {
                                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            },
                        )
                    }
                }
            }

            when (selectedTab) {
                HomeTab.AddProducts -> {
                    AddProductScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = productViewModel,
                    )
                }

                HomeTab.Recommendations -> {
                    RecommendationRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = recommendationViewModel,
                        bottomSheetPeek = updateBottomSheetPeek,
                    )
                }

                HomeTab.Compare -> {
                    CompareProductsRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = compareProductViewModel,
                        bottomSheetPeek = updateBottomSheetPeek,
                    )
                }
            }
        }
    }

    ModalBottomSheet(
        bottomSheet = bottomSheet,
        navigateToStore = navigateToStore,
        hideBottomSheet = hideBottomSheet,
        visitOnlineStore = visitOnlineStore,
        updateBottomSheet = updateBottomSheetPeek,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun MainUIPreview() {
    XentlyTheme {
        var selectedTab by remember {
            mutableStateOf(HomeTab.Recommendations)
        }
        val stateHandle = remember { SavedStateHandle() }
        MainUI(
            selectedTab = selectedTab,
            bottomSheet = { BottomSheet.Ignore },
            hideBottomSheet = { true },
            onTabClicked = { selectedTab = it },
            navigateToStore = {},
            visitOnlineStore = {},
            updateBottomSheetPeek = {},
            productViewModel = ProductViewModel(
                stateHandle = stateHandle,
                productRepository = ProductRepository.Fake,
            ),
            recommendationViewModel = RecommendationViewModel(
                stateHandle = stateHandle,
                repository = RecommendationRepository.Fake,
            ),
            compareProductViewModel = CompareProductViewModel(
                stateHandle = stateHandle,
                repository = CompareProductRepository.Fake,
            ),
        )
    }
}
