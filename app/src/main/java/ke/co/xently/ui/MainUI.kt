package ke.co.xently.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import ke.co.xently.BottomSheet
import ke.co.xently.HomeTab
import ke.co.xently.MainViewModel
import ke.co.xently.R
import ke.co.xently.features.attributes.repositories.AttributeRepository
import ke.co.xently.features.attributesvalues.repositories.AttributeValueRepository
import ke.co.xently.features.brands.repositories.BrandRepository
import ke.co.xently.features.compareproducts.repositories.CompareProductRepository
import ke.co.xently.features.compareproducts.ui.CompareProductViewModel
import ke.co.xently.features.compareproducts.ui.CompareProductsRequestScreen
import ke.co.xently.features.core.visitUriPage
import ke.co.xently.features.measurementunit.repositories.MeasurementUnitRepository
import ke.co.xently.features.products.repositories.ProductRepository
import ke.co.xently.features.products.ui.AddProductScreen
import ke.co.xently.features.products.ui.ProductViewModel
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.repositories.RecommendationRepository
import ke.co.xently.features.recommendations.ui.RecommendationRequestScreen
import ke.co.xently.features.recommendations.ui.RecommendationViewModel
import ke.co.xently.features.shop.repositories.ShopRepository
import ke.co.xently.features.store.repositories.StoreRepository
import ke.co.xently.ui.components.AnimatedModalBottomSheet
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.launch

private const val TAG = "MainUI"

@Composable
fun MainUI() {
    val viewModel = hiltViewModel<MainViewModel>()
    val selectedTab by viewModel.currentlyActiveTab.collectAsState()

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

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
        navigateToStore = navigateToStore,
        bottomSheet = { stackOfBottomSheets.firstOrNull() ?: BottomSheet.Ignore },
        snackbarHostState = snackbarHostState,
        onTabClicked = viewModel::saveCurrentlyActiveTab,
        hideBottomSheet = {
            stackOfBottomSheets.removeFirstOrNull() == null
        },
        updateBottomSheetPeek = {
            stackOfBottomSheets.add(0, it)
        },
        visitOnlineStore = { response ->
            response.store.shop.ecommerceSiteUrl.takeIf { !it.isNullOrBlank() }?.also {
                context.visitUriPage(it.trim(), logTag = TAG)
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainUI(
    selectedTab: HomeTab,
    snackbarHostState: SnackbarHostState,
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
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                for (tab in HomeTab.values()) {
                    Tab(
                        selected = selectedTab == tab,
                        onClick = {
                            onTabClicked(tab)
                        },
                        text = {
                            Text(stringResource(tab.title))
                        },
                    )
                }
            }

            when (selectedTab) {
                HomeTab.AddProducts -> {
                    AddProductScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = productViewModel,
                        snackbarHostState = snackbarHostState,
                    )
                }

                HomeTab.Recommendations -> {
                    RecommendationRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = recommendationViewModel,
                        snackbarHostState = snackbarHostState,
                        bottomSheetPeek = updateBottomSheetPeek,
                    )
                }

                HomeTab.Compare -> {
                    CompareProductsRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = compareProductViewModel,
                        snackbarHostState = snackbarHostState,
                        bottomSheetPeek = updateBottomSheetPeek,
                    )
                }
            }
        }
    }

    AnimatedModalBottomSheet(
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
        val snackbarHostState = remember { SnackbarHostState() }
        val stateHandle = remember { SavedStateHandle() }
        MainUI(
            selectedTab = selectedTab,
            snackbarHostState = snackbarHostState,
            bottomSheet = { BottomSheet.Ignore },
            onTabClicked = { selectedTab = it },
            navigateToStore = {},
            hideBottomSheet = { true },
            visitOnlineStore = {},
            updateBottomSheetPeek = {},
            productViewModel = ProductViewModel(
                stateHandle = stateHandle,
                productRepository = ProductRepository.Fake,
                storeRepository = StoreRepository.Fake,
                shopRepository = ShopRepository.Fake,
                brandRepository = BrandRepository.Fake,
                attributeRepository = AttributeRepository.Fake,
                attributeValueRepository = AttributeValueRepository.Fake,
                measurementUnitRepository = MeasurementUnitRepository.Fake,
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
