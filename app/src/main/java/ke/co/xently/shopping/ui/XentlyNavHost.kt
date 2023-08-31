package ke.co.xently.shopping.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ke.co.xently.shopping.LocalNavController
import ke.co.xently.shopping.features.authentication.ui.requestpasswordreset.RequestPasswordResetScreen
import ke.co.xently.shopping.features.authentication.ui.resetpassword.ResetPasswordScreen
import ke.co.xently.shopping.features.authentication.ui.signin.SignInScreen
import ke.co.xently.shopping.features.authentication.ui.signup.SignUpScreen


@Composable
fun XentlyNavHost() {
    val navController = LocalNavController.current
    val onSuccess: () -> Unit by rememberUpdatedState {
        navController.navigate(NavigationRoute.Main()) {
            launchSingleTop = true
            popUpTo(NavigationRoute.Main()) {
                inclusive = true
            }
        }
    }

    val onNavigateBack: () -> Unit by rememberUpdatedState {
        navController.navigateUp()
    }

    NavHost(navController = navController, startDestination = NavigationRoute.Main.route) {
        composable(NavigationRoute.Main.route) {
            MainUI()
        }

        composable(NavigationRoute.SignIn.route) {
            SignInScreen(
                onSuccess = onSuccess,
                onNavigateBack = onNavigateBack,
                requestRegistration = { navController.navigate(NavigationRoute.SignUp()) },
                forgetPassword = { navController.navigate(NavigationRoute.RequestPasswordReset()) },
            )
        }

        composable(NavigationRoute.SignUp.route) {
            SignUpScreen(
                onSuccess = onSuccess,
                onNavigateBack = onNavigateBack,
            )
        }

        composable(NavigationRoute.RequestPasswordReset.route) {
            RequestPasswordResetScreen(
                onSuccess = onSuccess,
                onNavigateBack = onNavigateBack,
            )
        }

        composable(NavigationRoute.ResetPassword.route) {
            ResetPasswordScreen(
                onSuccess = onSuccess,
                onNavigateBack = onNavigateBack,
            )
        }
    }

}