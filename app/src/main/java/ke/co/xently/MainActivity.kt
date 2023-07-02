package ke.co.xently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.features.products.ui.AddProductScreen
import ke.co.xently.features.recommendations.ui.RecommendationRequestScreen
import ke.co.xently.ui.theme.XentlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XentlyTheme {
                val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

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
                ) { paddingValues ->
                    val viewModel = hiltViewModel<MainViewModel>()
                    val selectedTab by viewModel.currentlyActiveTab.collectAsState()
                    Column(modifier = Modifier.padding(paddingValues)) {
                        TabRow(selectedTabIndex = selectedTab.ordinal) {
                            for (tab in HomeTab.values()) {
                                Tab(
                                    selected = selectedTab == tab,
                                    onClick = {
                                        viewModel.saveCurrentlyActiveTab(tab)
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

                            HomeTab.GetRecommendations -> {
                                RecommendationRequestScreen(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}