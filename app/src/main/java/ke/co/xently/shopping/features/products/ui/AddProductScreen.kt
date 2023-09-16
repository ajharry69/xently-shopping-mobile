package ke.co.xently.shopping.features.products.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.features.attributesvalues.models.AttributeValue
import ke.co.xently.shopping.features.attributesvalues.ui.AddAttributesPage
import ke.co.xently.shopping.features.brands.models.Brand
import ke.co.xently.shopping.features.brands.ui.AddBrandsPage
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import ke.co.xently.shopping.features.locationtracker.LocalFlowOfSaveProductState
import ke.co.xently.shopping.features.locationtracker.LocalFlowOfTraversedSteps
import ke.co.xently.shopping.features.measurementunit.ui.AddMeasurementUnitNamePage
import ke.co.xently.shopping.features.measurementunit.ui.AddMeasurementUnitQuantityPage
import ke.co.xently.shopping.features.products.models.Product
import ke.co.xently.shopping.features.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.shopping.features.products.ui.subscreens.AddProductNamePage
import ke.co.xently.shopping.features.products.ui.subscreens.SummaryPage
import ke.co.xently.shopping.features.shop.models.Shop
import ke.co.xently.shopping.features.shop.ui.AddShopPage
import ke.co.xently.shopping.features.store.models.Store
import ke.co.xently.shopping.features.store.ui.AddStorePage
import kotlin.random.Random

@Composable
fun AddProductScreen(modifier: Modifier, viewModel: ProductViewModel) {
    val currentlyActiveStep by viewModel.currentlyActiveStep.collectAsState()

    CompositionLocalProvider(
        LocalAddProductStep provides currentlyActiveStep,
        LocalFlowOfTraversedSteps provides viewModel.traversedSteps,
        LocalFlowOfSaveProductState provides viewModel.flowOfSaveProductState,
    ) {
        val product by viewModel.product.collectAsState()

        AddProductScreen(
            modifier = modifier,
            product = product,
            savePermanently = viewModel::savePermanently,
            saveDraft = viewModel::saveDraft,
            saveCurrentlyActiveStep = viewModel::saveCurrentlyActiveStep,
        )
    }
}

@Composable
fun AddProductScreen(
    modifier: Modifier,
    product: Product.LocalViewModel,
    savePermanently: (Array<AddProductStep>) -> Unit,
    saveDraft: (Product.LocalViewModel) -> Unit,
    saveCurrentlyActiveStep: (AddProductStep) -> Unit,
) {
    val currentlyActiveStep = LocalAddProductStep.current
    val navigateToNext: () -> Unit = {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(currentlyActiveStep.ordinal + 1)
            .also(saveCurrentlyActiveStep)
    }

    val navigateToPrevious: () -> Unit = {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(currentlyActiveStep.ordinal - 1)
            .also(saveCurrentlyActiveStep)
    }

    Column(modifier = Modifier.then(modifier)) {
        ScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = currentlyActiveStep.ordinal,
        ) {
            val traversed by LocalFlowOfTraversedSteps.current.collectAsState(initial = emptySet())
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
        AnimatedContent(
            targetState = currentlyActiveStep,
            label = "AddProductAnimatedContent"
        ) { step ->
            when (step) {
                AddProductStep.Store -> {
                    val productDraft: (Store) -> Product.LocalViewModel = {
                        product.copy(store = it.toLocalViewModel())
                    }
                    AddStorePage(
                        modifier = Modifier.fillMaxSize(),
                        store = product.store,
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
                    val productDraft: (Shop) -> Product.LocalViewModel = {
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
                    val productDraft: (Product) -> Product.LocalViewModel = {
                        val productViewModel = it.toLocalViewModel()
                        product.copy(
                            name = productViewModel.name,
                            brands = productViewModel.brands,
                            attributeValues = productViewModel.attributeValues,
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

                AddProductStep.MeasurementUnitName -> {
                    val productDraft: (Product) -> Product.LocalViewModel = {
                        val productViewModel = it.toLocalViewModel()
                        product.copy(
                            name = productViewModel.name,
                            brands = productViewModel.brands,
                            attributeValues = productViewModel.attributeValues,
                            measurementUnit = productViewModel.measurementUnit,
                            measurementUnitQuantity = productViewModel.measurementUnitQuantity,
                            autoFillMeasurementUnitNamePlural = productViewModel.autoFillMeasurementUnitNamePlural,
                            autoFillMeasurementUnitSymbolPlural = productViewModel.autoFillMeasurementUnitSymbolPlural,
                        )
                    }
                    AddMeasurementUnitNamePage(
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

                AddProductStep.MeasurementUnitQuantity -> {
                    val productDraft: (Product) -> Product.LocalViewModel = {
                        val productViewModel = it.toLocalViewModel()
                        product.copy(
                            name = productViewModel.name,
                            brands = productViewModel.brands,
                            attributeValues = productViewModel.attributeValues,
                            measurementUnit = productViewModel.measurementUnit,
                            measurementUnitQuantity = productViewModel.measurementUnitQuantity,
                            autoFillMeasurementUnitNamePlural = productViewModel.autoFillMeasurementUnitNamePlural,
                            autoFillMeasurementUnitSymbolPlural = productViewModel.autoFillMeasurementUnitSymbolPlural,
                        )
                    }
                    AddMeasurementUnitQuantityPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
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
                    val productDraft: (List<Brand>) -> Product.LocalViewModel = {
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
                    val productDraft: (List<AttributeValue>) -> Product.LocalViewModel = {
                        product.copy(attributeValues = it.map(AttributeValue::toLocalViewModel))
                    }
                    AddAttributesPage(
                        modifier = Modifier.fillMaxSize(),
                        attributes = product.attributeValues,
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
                product = Product.LocalViewModel.default,
                savePermanently = {},
                saveDraft = {},
            ) {}
        }
    }
}