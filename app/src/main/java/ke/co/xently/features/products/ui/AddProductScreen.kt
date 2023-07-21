package ke.co.xently.features.products.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.attributesvalues.ui.AddAttributesPage
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.features.brands.ui.AddBrandsPage
import ke.co.xently.features.locationtracker.LocationPermissionsState
import ke.co.xently.features.measurementunit.ui.AddMeasurementUnitPage
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.features.products.ui.subscreens.AddProductNamePage
import ke.co.xently.features.products.ui.subscreens.SummaryPage
import ke.co.xently.features.shop.models.Shop
import ke.co.xently.features.shop.ui.AddShopPage
import ke.co.xently.features.store.models.Store
import ke.co.xently.features.store.ui.AddStorePage
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

@Composable
fun AddProductScreen(modifier: Modifier, viewModel: ProductViewModel) {
    val currentlyActiveStep by viewModel.currentlyActiveStep.collectAsState()
    val product by viewModel.product.collectAsState()

    CompositionLocalProvider(LocalAddProductStep provides currentlyActiveStep) {
        AddProductScreen(
            modifier = modifier,
            locationPermissionsState = LocationPermissionsState.CoarseAndFine,
            product = product,
            traversedSteps = viewModel.traversedSteps,
            addProductState = viewModel.saveProductStateFlow,

            savePermanently = viewModel::savePermanently,
            saveDraft = viewModel::saveDraft,
            saveCurrentlyActiveStep = viewModel::saveCurrentlyActiveStep,
        )
    }
}

@Composable
fun AddProductScreen(
    modifier: Modifier,
    locationPermissionsState: LocationPermissionsState,
    product: Product.LocalViewModel,
    traversedSteps: Flow<Set<AddProductStep>>,
    addProductState: Flow<State>,

    savePermanently: (Array<AddProductStep>) -> Unit,
    saveDraft: (Product.LocalViewModel) -> Unit,
    saveCurrentlyActiveStep: (AddProductStep) -> Unit,
) {
    val currentlyActiveStep = LocalAddProductStep.current
    val navigateToNext: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(currentlyActiveStep.ordinal + 1)
            .also(saveCurrentlyActiveStep)
    }

    val navigateToPrevious: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(currentlyActiveStep.ordinal - 1)
            .also(saveCurrentlyActiveStep)
    }

    Column(modifier = Modifier.then(modifier)) {
        ScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = currentlyActiveStep.ordinal,
        ) {
            val traversed by traversedSteps.collectAsState(initial = emptySet())
            for (step in AddProductStep.values()) {
                val enabled = step == currentlyActiveStep || step in traversed
                Tab(
                    selected = step.ordinal <= currentlyActiveStep.ordinal,
                    enabled = enabled,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.run {
                        if (enabled) {
                            this
                        } else {
                            copy(alpha = 0.38f)
                        }
                    },
                    onClick = {
                        saveCurrentlyActiveStep(step)
                    },
                    text = {
                        Text(
                            stringResource(step.title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
        AnimatedContent(targetState = currentlyActiveStep) { step ->
            when (step) {
                AddProductStep.Store -> {
                    val productDraft: (Store) -> Product.LocalViewModel by rememberUpdatedState {
                        product.copy(store = it.toLocalViewModel())
                    }
                    AddStorePage(
                        modifier = Modifier.fillMaxSize(),
                        store = product.store,
                        permissionsState = locationPermissionsState,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                    ) {
                        productDraft(it)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Shop -> {
                    val productDraft: (Shop) -> Product.LocalViewModel by rememberUpdatedState {
                        product.run {
                            copy(store = store.copy(shop = it.toLocalViewModel()))
                        }
                    }
                    AddShopPage(
                        modifier = Modifier.fillMaxSize(),
                        shop = product.store.shop,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onPreviousClick = navigateToPrevious,
                    ) {
                        productDraft(it)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.ProductName -> {
                    val productDraft: (Product) -> Product.LocalViewModel by rememberUpdatedState {
                        val productViewModel = it.toLocalViewModel()
                        product.copy(
                            name = productViewModel.name,
                            brands = productViewModel.brands,
                            attributes = productViewModel.attributes,
                            measurementUnit = productViewModel.measurementUnit,
                            measurementUnitQuantity = productViewModel.measurementUnitQuantity,
                            autoFillNamePlural = productViewModel.autoFillNamePlural,
                        )
                    }
                    AddProductNamePage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onPreviousClick = navigateToPrevious,
                    ) {
                        productDraft(it)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.MeasurementUnit -> {
                    val productDraft: (Product) -> Product.LocalViewModel by rememberUpdatedState {
                        val productViewModel = it.toLocalViewModel()
                        product.copy(
                            name = productViewModel.name,
                            brands = productViewModel.brands,
                            attributes = productViewModel.attributes,
                            measurementUnit = productViewModel.measurementUnit,
                            measurementUnitQuantity = productViewModel.measurementUnitQuantity,
                            autoFillMeasurementUnitNamePlural = productViewModel.autoFillMeasurementUnitNamePlural,
                            autoFillMeasurementUnitSymbolPlural = productViewModel.autoFillMeasurementUnitSymbolPlural,
                        )
                    }
                    AddMeasurementUnitPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        onSuggestionSelected = {
                            productDraft(product.copy(measurementUnit = it.toLocalViewModel()))
                                .let(saveDraft)
                        },
                        onPreviousClick = navigateToPrevious,
                    ) {
                        productDraft(it)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.GeneralDetails -> {
                    AddGeneralDetailsPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        it.toLocalViewModel()
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Brands -> {
                    val productDraft: (List<Brand>) -> Product.LocalViewModel by rememberUpdatedState {
                        product.copy(brands = it.map(Brand::toLocalViewModel))
                    }
                    AddBrandsPage(
                        modifier = Modifier.fillMaxSize(),
                        brands = product.brands,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onPreviousClick = navigateToPrevious,
                    ) { brands ->
                        productDraft(brands)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Attributes -> {
                    val productDraft: (List<AttributeValue>) -> Product.LocalViewModel by rememberUpdatedState {
                        product.copy(attributes = it.map(AttributeValue::toLocalViewModel))
                    }
                    AddAttributesPage(
                        modifier = Modifier.fillMaxSize(),
                        attributes = product.attributes,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onPreviousClick = navigateToPrevious,
                    ) { attributes ->
                        productDraft(attributes)
                            .let(saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Summary -> {
                    SummaryPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        stateFlow = addProductState,
                        onPreviousClick = navigateToPrevious,
                        onSubmissionSuccess = navigateToNext,
                        submit = savePermanently,
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddProductScreenPreview() {
    XentlyTheme {
        CompositionLocalProvider(
            LocalAddProductStep provides AddProductStep.valueOfOrdinalOrFirstByOrdinal(
                Random.nextInt(
                    AddProductStep.values().size
                )
            ),
        ) {
            AddProductScreen(
                modifier = Modifier.fillMaxSize(),
                locationPermissionsState = LocationPermissionsState.Simulated,
                product = Product.LocalViewModel.default,
                traversedSteps = MutableStateFlow(emptySet()),
                addProductState = flowOf(State.Idle),

                savePermanently = {},
                saveDraft = {},
            ) {}
        }
    }
}