package ke.co.xently.shopping.ui

sealed class NavigationRoute(val route: String) {
    operator fun invoke(vararg arguments: Pair<String, Any>): String {
        var r = route
        for ((key, value) in arguments) {
            r = r.replace("{$key}", value.toString())
        }
        return r
    }

    object Main : NavigationRoute(route = "main")
    object SignIn : NavigationRoute(route = "sign-in")
    object SignUp : NavigationRoute(route = "sign-up")
    object RequestPasswordReset : NavigationRoute(route = "request-password-reset")
    object ResetPassword : NavigationRoute(route = "reset-password")
}