package ke.co.xently.shopping

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import ke.co.xently.shopping.features.users.User


val LocalSnackbarHostState = staticCompositionLocalOf {
    SnackbarHostState()
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalCurrentlySignInUser = compositionLocalOf<User?> {
    null
}