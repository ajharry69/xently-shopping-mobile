package ke.co.xently.shopping.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import ke.co.xently.shopping.BottomSheet
import ke.co.xently.shopping.HomeTab
import ke.co.xently.shopping.LocalCurrentlySignInUser
import ke.co.xently.shopping.LocalNavController
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.MainViewModel
import ke.co.xently.shopping.NavigationRoute
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.compareproducts.repositories.CompareProductRepository
import ke.co.xently.shopping.features.compareproducts.ui.CompareProductViewModel
import ke.co.xently.shopping.features.compareproducts.ui.CompareProductsRequestScreen
import ke.co.xently.shopping.features.core.PRIVACY_POLICY_URL
import ke.co.xently.shopping.features.core.TERMS_OF_SERVICE_URL
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.core.visitUriPage
import ke.co.xently.shopping.features.products.repositories.ProductRepository
import ke.co.xently.shopping.features.products.ui.AddProductScreen
import ke.co.xently.shopping.features.products.ui.ProductViewModel
import ke.co.xently.shopping.features.recommendations.repositories.RecommendationRepository
import ke.co.xently.shopping.features.recommendations.ui.RecommendationRequestScreen
import ke.co.xently.shopping.features.recommendations.ui.RecommendationViewModel
import ke.co.xently.shopping.ui.components.ModalBottomSheet

@Composable
fun MainUI() {
    val viewModel = hiltViewModel<MainViewModel>()
    val selectedTab by viewModel.currentlyActiveTab.collectAsState()

    val snackbarHostState = LocalSnackbarHostState.current

    val context = LocalContext.current

    val navController = LocalNavController.current

    val user = LocalCurrentlySignInUser.current

    LaunchedEffect(user) {
        if (user == null) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = context.getString(R.string.xently_notification_auth_session_expired),
                actionLabel = context.getString(R.string.xently_page_title_sign_in)
                    .toUpperCase(Locale.current),
                duration = SnackbarDuration.Indefinite,
            )

            if (snackbarResult == SnackbarResult.ActionPerformed) {
                navController.navigate(NavigationRoute.SignIn())
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
        onPrivacyPolicyClicked = {
            context.visitUriPage(PRIVACY_POLICY_URL, logTag = "MainScreen")
        },
        onTermsOfServiceClicked = {
            context.visitUriPage(TERMS_OF_SERVICE_URL, logTag = "MainScreen")
        },
        onLogoutOrLoginClicked = {
            if (user == null) {
                navController.navigate(NavigationRoute.SignIn())
            } else {
                viewModel.signOut()
            }
        },
        onRecommendationRequestSuccess = {
            navController.navigate(NavigationRoute.Recommendations())
        },
        onTabClicked = viewModel::saveCurrentlyActiveTab,
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
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfServiceClicked: () -> Unit,
    onLogoutOrLoginClicked: () -> Unit,
    onRecommendationRequestSuccess: () -> Unit,
    onTabClicked: (HomeTab) -> Unit,
    updateBottomSheetPeek: (BottomSheet) -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    recommendationViewModel: RecommendationViewModel = hiltViewModel(),
    compareProductViewModel: CompareProductViewModel = hiltViewModel(),
) {
    val user = LocalCurrentlySignInUser.current

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
                        letterSpacing = TextUnit(4f, TextUnitType.Sp),
                    )
                },
                actions = {
                    var showMenu by rememberSaveable {
                        mutableStateOf(false)
                    }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu options")
                    }


                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            onClick = { onLogoutOrLoginClicked(); showMenu = false },
                            text = {
                                val label = if (user == null) {
                                    R.string.xently_menu_item_signin
                                } else {
                                    R.string.xently_menu_item_signout
                                }

                                Text(text = stringResource(label))
                            },
                        )
                        DropdownMenuItem(
                            onClick = { onPrivacyPolicyClicked(); showMenu = false },
                            text = {
                                Text(text = stringResource(R.string.xently_content_description_privacy_policy))
                            },
                        )
                        DropdownMenuItem(
                            onClick = { onTermsOfServiceClicked(); showMenu = false },
                            text = {
                                Text(text = stringResource(R.string.xently_content_description_terms_of_service))
                            },
                        )
                    }
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
                        onSuccess = onRecommendationRequestSuccess,
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

    ModalBottomSheet(bottomSheet = bottomSheet, hideBottomSheet = hideBottomSheet)
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
            onPrivacyPolicyClicked = {},
            onTermsOfServiceClicked = {},
            onLogoutOrLoginClicked = {},
            onRecommendationRequestSuccess = {},
            onTabClicked = { selectedTab = it },
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
