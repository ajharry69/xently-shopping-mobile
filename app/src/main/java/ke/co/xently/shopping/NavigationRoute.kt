package ke.co.xently.shopping

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
    object Recommendations : NavigationRoute(route = "recommendations")
    object MpesaCheckout :
        NavigationRoute(route = "checkout-with-mpesa/{${Argument.ServiceCharge.name}}") {
        enum class Argument(
            val type: NavType<*>,
            val defaultValue: Any? = null,
            val nullable: Boolean = true,
        ) {
            ServiceCharge(NavType.StringType);

            companion object {
                val arguments: List<NamedNavArgument> = buildList {
                    Argument.values().forEach {
                        navArgument(it.name) {
                            defaultValue = it.defaultValue
                            nullable = it.nullable
                            type = it.type
                        }.let(::add)
                    }
                }
            }
        }
    }
}