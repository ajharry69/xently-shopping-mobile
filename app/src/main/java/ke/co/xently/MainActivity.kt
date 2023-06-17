package ke.co.xently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.products.ui.AddProductScreen
import ke.co.xently.ui.theme.XentlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XentlyTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(stringResource(R.string.app_name))
                                },
                            )
                        },
                    ) { paddingValues ->
                        var selectedTab by remember { mutableStateOf(HomeTab.GetRecommendations) }
                        Column(modifier = Modifier.padding(paddingValues)) {
                            TabRow(selectedTabIndex = selectedTab.ordinal) {
                                for (tab in HomeTab.values()) {
                                    Tab(
                                        selected = selectedTab == tab,
                                        onClick = {
                                            selectedTab = tab
                                        },
                                        text = {
                                            Text(stringResource(tab.title))
                                        },
                                    )
                                }
                            }

                            when (selectedTab) {
                                HomeTab.AddProducts -> {
                                    AddProductScreen(modifier = Modifier.fillMaxSize())
                                }

                                HomeTab.GetRecommendations -> {
                                    Text("Get recommendations...")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}