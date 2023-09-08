package ke.co.xently.shopping

import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ke.co.xently.shopping.features.authentication.ui.requestpasswordreset.RequestPasswordResetScreen
import ke.co.xently.shopping.features.authentication.ui.resetpassword.ResetPasswordScreen
import ke.co.xently.shopping.features.authentication.ui.signin.SignInScreen
import ke.co.xently.shopping.features.authentication.ui.signup.SignUpScreen
import ke.co.xently.shopping.features.collectpayments.ui.MpesaPaymentRequestScreen
import ke.co.xently.shopping.features.recommendations.ui.response.RecommendationResponseScreen
import ke.co.xently.shopping.features.recommendations.ui.response.navigateToStore
import ke.co.xently.shopping.features.recommendations.ui.response.visitOnlineStore
import ke.co.xently.shopping.ui.MainUI
import java.math.BigDecimal


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

    NavHost(
        modifier = Modifier
            .navigationBarsPadding()
            .systemBarsPadding()
            .mandatorySystemGesturesPadding()
            .safeContentPadding(),
        navController = navController,
        startDestination = NavigationRoute.Main.route
    ) {
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

        composable(NavigationRoute.Recommendations.route) {
            RecommendationResponseScreen(
                navigateToStore = navigateToStore(),
                visitOnlineStore = visitOnlineStore(),
                onNavigateBack = onNavigateBack,
                onPaymentRequest = {
                    navController.navigate(NavigationRoute.MpesaCheckout(NavigationRoute.MpesaCheckout.Argument.ServiceCharge.name to it))
                },
            )
        }

        composable(
            NavigationRoute.MpesaCheckout.route,
            arguments = NavigationRoute.MpesaCheckout.Argument.arguments,
        ) {
            val serviceCharge = it.arguments!!
                .getString(NavigationRoute.MpesaCheckout.Argument.ServiceCharge.name)
            MpesaPaymentRequestScreen(
                onSuccess = onNavigateBack,
                onNavigateBack = onNavigateBack,
                serviceCharge = BigDecimal(serviceCharge),
            )
        }
    }

}