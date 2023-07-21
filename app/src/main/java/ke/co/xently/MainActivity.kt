package ke.co.xently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.ui.MainUI
import ke.co.xently.ui.theme.XentlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XentlyTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                    MainUI()
                }
            }
        }
    }
}
