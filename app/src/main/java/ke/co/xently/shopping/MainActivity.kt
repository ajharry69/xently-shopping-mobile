package ke.co.xently.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.shopping.features.attributes.datasources.remoteservices.AttributeAutoCompleteService
import ke.co.xently.shopping.features.attributes.ui.LocalAttributeAutoCompleteService
import ke.co.xently.shopping.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService
import ke.co.xently.shopping.features.attributesvalues.ui.LocalAttributeValueAutoCompleteService
import ke.co.xently.shopping.features.authentication.workers.DeleteCurrentlySignedInUserOnSessionExpirationWorker
import ke.co.xently.shopping.features.brands.datasources.remoteservices.BrandAutoCompleteService
import ke.co.xently.shopping.features.brands.ui.LocalBrandAutoCompleteService
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.locationtracker.LocalLocationPermissionsState
import ke.co.xently.shopping.features.locationtracker.LocationPermissionsState
import ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService
import ke.co.xently.shopping.features.measurementunit.ui.LocalMeasurementUnitAutoCompleteService
import ke.co.xently.shopping.features.products.datasources.remoteservices.ProductAutoCompleteService
import ke.co.xently.shopping.features.products.ui.LocalProductAutoCompleteService
import ke.co.xently.shopping.features.shop.datasources.remoteservices.ShopAutoCompleteService
import ke.co.xently.shopping.features.shop.ui.LocalShopAutoCompleteService
import ke.co.xently.shopping.features.store.datasources.remoteservices.StoreAutoCompleteService
import ke.co.xently.shopping.features.store.ui.LocalStoreAutoCompleteService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var shopAutoCompleteService: ShopAutoCompleteService

    @Inject
    lateinit var storeAutoCompleteService: StoreAutoCompleteService

    @Inject
    lateinit var brandAutoCompleteService: BrandAutoCompleteService

    @Inject
    lateinit var productAutoCompleteService: ProductAutoCompleteService

    @Inject
    lateinit var attributeAutoCompleteService: AttributeAutoCompleteService

    @Inject
    lateinit var attributeValueAutoCompleteService: AttributeValueAutoCompleteService

    @Inject
    lateinit var measurementUnitAutoCompleteService: MeasurementUnitAutoCompleteService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request =
            PeriodicWorkRequestBuilder<DeleteCurrentlySignedInUserOnSessionExpirationWorker>(
                15, TimeUnit.SECONDS
            ).addTag(DeleteCurrentlySignedInUserOnSessionExpirationWorker.TAG)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance(this)
            .enqueue(request)

        setContent {
            XentlyTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                val viewModel = hiltViewModel<MainViewModel>()

                val currentlySignInUser by viewModel.currentlySignInUser.collectAsState()

                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalSnackbarHostState provides snackbarHostState,
                    LocalCurrentlySignInUser provides currentlySignInUser,
                    LocalLocationPermissionsState provides LocationPermissionsState.CoarseAndFine,
                    LocalProductAutoCompleteService provides productAutoCompleteService,
                    LocalStoreAutoCompleteService provides storeAutoCompleteService,
                    LocalShopAutoCompleteService provides shopAutoCompleteService,
                    LocalMeasurementUnitAutoCompleteService provides measurementUnitAutoCompleteService,
                    LocalBrandAutoCompleteService provides brandAutoCompleteService,
                    LocalAttributeAutoCompleteService provides attributeAutoCompleteService,
                    LocalAttributeValueAutoCompleteService provides attributeValueAutoCompleteService,
                ) {
                    XentlyNavHost()
                }
            }
        }
    }
}
