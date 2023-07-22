package ke.co.xently.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.shopping.features.attributes.datasources.remoteservices.AttributeAutoCompleteService
import ke.co.xently.shopping.features.attributes.ui.LocalAttributeAutoCompleteService
import ke.co.xently.shopping.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService
import ke.co.xently.shopping.features.attributesvalues.ui.LocalAttributeValueAutoCompleteService
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
import ke.co.xently.shopping.ui.MainUI
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
        setContent {
            XentlyTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState,
                    LocalLocationPermissionsState provides LocationPermissionsState.CoarseAndFine,
                    LocalProductAutoCompleteService provides productAutoCompleteService,
                    LocalStoreAutoCompleteService provides storeAutoCompleteService,
                    LocalShopAutoCompleteService provides shopAutoCompleteService,
                    LocalMeasurementUnitAutoCompleteService provides measurementUnitAutoCompleteService,
                    LocalBrandAutoCompleteService provides brandAutoCompleteService,
                    LocalAttributeAutoCompleteService provides attributeAutoCompleteService,
                    LocalAttributeValueAutoCompleteService provides attributeValueAutoCompleteService,
                ) {
                    MainUI()
                }
            }
        }
    }
}
