package ke.co.xently.shopping.features.recommendations.ui.response

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import ke.co.xently.shopping.LocalSnackbarHostState
import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.visitUriPage
import ke.co.xently.shopping.features.recommendations.models.Recommendation
import kotlinx.coroutines.launch


@Composable
fun visitOnlineStore(): (Recommendation.Response) -> Unit {
    val context = LocalContext.current
    return { response ->
        response.store.shop.ecommerceSiteUrl.takeIf { !it.isNullOrBlank() }?.also {
            context.visitUriPage(it.trim(), logTag = "RecommendationResponseScreen")
        }
    }
}

@Composable
fun navigateToStore(): (Recommendation.Response) -> Unit {
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    return { recommendation ->
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
                    context.visitUriPage(
                        "market://details?id=$googleMapsPackageName",
                        logTag = "RecommendationResponseScreen",
                        onActivityNotFound = {
                            context.visitUriPage(
                                "https://play.google.com/store/apps/details?id=$googleMapsPackageName",
                                logTag = "RecommendationResponseScreen",
                            )
                        },
                    )
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
}