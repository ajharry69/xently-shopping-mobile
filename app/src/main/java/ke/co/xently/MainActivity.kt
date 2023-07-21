package ke.co.xently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import ke.co.xently.features.attributes.datasources.remoteservices.AttributeAutoCompleteService
import ke.co.xently.features.attributes.ui.LocalAttributeAutoCompleteService
import ke.co.xently.features.attributesvalues.datasources.remoteservices.AttributeValueAutoCompleteService
import ke.co.xently.features.attributesvalues.ui.LocalAttributeValueAutoCompleteService
import ke.co.xently.features.brands.datasources.remoteservices.BrandAutoCompleteService
import ke.co.xently.features.brands.ui.LocalBrandAutoCompleteService
import ke.co.xently.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService
import ke.co.xently.features.measurementunit.ui.LocalMeasurementUnitAutoCompleteService
import ke.co.xently.features.products.datasources.remoteservices.ProductAutoCompleteService
import ke.co.xently.features.products.ui.LocalProductAutoCompleteService
import ke.co.xently.features.shop.datasources.remoteservices.ShopAutoCompleteService
import ke.co.xently.features.shop.ui.LocalShopAutoCompleteService
import ke.co.xently.features.store.datasources.remoteservices.StoreAutoCompleteService
import ke.co.xently.features.store.ui.LocalStoreAutoCompleteService
import ke.co.xently.ui.MainUI
import ke.co.xently.ui.theme.XentlyTheme
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
