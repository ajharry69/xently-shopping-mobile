package ke.co.xently.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.BottomSheetPeek
import ke.co.xently.HomeTab
import ke.co.xently.MainViewModel
import ke.co.xently.R
import ke.co.xently.features.compareproducts.ui.CompareProductResponseScreen
import ke.co.xently.features.compareproducts.ui.CompareProductsRequestScreen
import ke.co.xently.features.products.ui.AddProductScreen
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.ui.RecommendationRequestScreen
import ke.co.xently.features.recommendations.ui.RecommendationResponseDetailsScreen
import ke.co.xently.features.recommendations.ui.RecommendationResponseScreen
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.launch


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
                    try {
                        Uri.parse("market://details?id=$googleMapsPackageName")
                    } catch (ex: ActivityNotFoundException) {
                        Uri.parse("https://play.google.com/store/apps/details?id=$googleMapsPackageName")
                    }.also {
                        context.startActivity(Intent(Intent.ACTION_VIEW, it))
                    }
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

    MainUI(
        selectedTab = selectedTab,
        snackbarHostState = snackbarHostState,
        navigateToStore = navigateToStore,
        onTabClicked = viewModel::saveCurrentlyActiveTab,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainUI(
    selectedTab: HomeTab,
    snackbarHostState: SnackbarHostState,
    onTabClicked: (HomeTab) -> Unit,
    navigateToStore: (Recommendation.Response) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState {
        when (it) {
            SheetValue.Hidden -> true
            SheetValue.Expanded -> true
            SheetValue.PartiallyExpanded -> true
        }
    }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
        snackbarHostState = snackbarHostState,
    )

    var sheetPeekHeight: Number by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == SheetValue.Hidden) {
            sheetPeekHeight = 0
        }
    }

    val peekHeight by animateDpAsState(targetValue = sheetPeekHeight.toFloat().dp)

    var bottomSheetPeek: BottomSheetPeek by remember {
        mutableStateOf(BottomSheetPeek.Ignore)
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(bottomSheetPeek) {
        sheetPeekHeight = when (val peek = bottomSheetPeek) {
            is BottomSheetPeek.Ignore -> 0
            is BottomSheetPeek.CompareProductResponse -> screenHeight / 2
            is BottomSheetPeek.RecommendationResponse.Single -> screenHeight / 2
            is BottomSheetPeek.RecommendationResponse.Many -> {
                if (peek.data.isEmpty()) {
                    0
                } else {
                    screenHeight / 2
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetShadowElevation = BottomSheetDefaults.Elevation * 5,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
            )
        },
        sheetContent = {
            when (val peek = bottomSheetPeek) {
                is BottomSheetPeek.Ignore -> {

                }

                is BottomSheetPeek.CompareProductResponse -> {
                    CompareProductResponseScreen(
                        modifier = Modifier,
                        response = peek.data,
                    )
                }

                is BottomSheetPeek.RecommendationResponse.Many -> {
                    RecommendationResponseScreen(
                        modifier = Modifier,
                        response = peek.data,
                        onNavigate = navigateToStore,
                        onViewProduct = {
                            bottomSheetPeek =
                                BottomSheetPeek.RecommendationResponse.Single(it)
                        },
                    )
                }

                is BottomSheetPeek.RecommendationResponse.Single -> {
                    RecommendationResponseDetailsScreen(
                        modifier = Modifier,
                        response = peek.data,
                        onNavigate = navigateToStore,
                    )
                }
            }
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
                        snackbarHostState = snackbarHostState,
                    )
                }

                HomeTab.Recommendations -> {
                    RecommendationRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState,
                        bottomSheetPeek = { bottomSheetPeek = it },
                    )
                }

                HomeTab.Compare -> {
                    CompareProductsRequestScreen(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState,
                        bottomSheetPeek = { bottomSheetPeek = it },
                    )
                }
            }
        }
    }
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
        MainUI(
            selectedTab = selectedTab,
            snackbarHostState = snackbarHostState,
            onTabClicked = { selectedTab = it },
            navigateToStore = {},
        )
    }
}
