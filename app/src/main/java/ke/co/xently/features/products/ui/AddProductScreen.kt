package ke.co.xently.features.products.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.features.attributes.models.Attribute
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.brands.models.Brand
import ke.co.xently.features.measurementunit.models.MeasurementUnit
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.models.ProductName
import ke.co.xently.features.products.ui.subscreens.AddAttributesPage
import ke.co.xently.features.products.ui.subscreens.AddBrandsPage
import ke.co.xently.features.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.features.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.features.products.ui.subscreens.AddProductNamePage
import ke.co.xently.features.products.ui.subscreens.AddShopPage
import ke.co.xently.features.products.ui.subscreens.AddStorePage
import ke.co.xently.features.products.ui.subscreens.SummaryPage
import ke.co.xently.features.shop.models.Shop
import ke.co.xently.features.store.models.Store
import ke.co.xently.locationtracker.LocationPermissionsState
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddProductScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val addProductStep: AddProductStep by viewModel.currentlyActiveStep.collectAsState()
    val product: Product.LocalViewModel by viewModel.product.collectAsState()

    AddProductScreen(
        modifier = modifier,
        product = product,
        addProductStep = addProductStep,
        locationPermissionsState = LocationPermissionsState.CoarseAndFine,
        snackbarHostState = snackbarHostState,
        brandSuggestionsState = viewModel.brandSuggestions,
        addProductState = viewModel.saveProductState,
        attributeSuggestionsState = viewModel.attributeSuggestions,
        attributeValueSuggestionsState = viewModel.attributeValueSuggestions,
        storeSuggestionsState = viewModel.storeSuggestions,
        shopSuggestionsState = viewModel.shopSuggestions,
        productSuggestionsState = viewModel.productSuggestions,
        savePermanently = viewModel::savePermanently,
        saveDraft = viewModel::saveDraft,
        saveCurrentlyActiveStep = viewModel::saveCurrentlyActiveStep,
        searchBrands = viewModel::searchBrand,
        searchAttributeValue = viewModel::searchAttributeValue,
        searchAttribute = viewModel::searchAttribute,
        searchStores = viewModel::searchStore,
        onStoreSearchSuggestionSelected = viewModel::clearStoreSearchSuggestions,
        searchShops = viewModel::searchShop,
        onShopSearchSuggestionSelected = viewModel::clearShopSearchSuggestions,
        searchProductNames = viewModel::searchProductName,
        onProductSearchSuggestionSelected = viewModel::clearProductSearchSuggestions,
        measurementUnitSuggestionsState = viewModel.measurementUnitSuggestions,
        searchMeasurementUnits = viewModel::searchMeasurementUnit,
        onMeasurementUnitSearchSuggestionSelected = viewModel::clearMeasurementUnitSearchSuggestions,
        onBrandSearchSuggestionSelected = { viewModel.clearBrandSearchSuggestions() },
        onAttributeValueSuggestionClicked = { viewModel.clearAttributeValueSuggestions() },
        onAttributeSuggestionClicked = { viewModel.clearAttributeSuggestions() },
    )
}

@Composable
fun AddProductScreen(
    modifier: Modifier,
    addProductStep: AddProductStep,
    locationPermissionsState: LocationPermissionsState,
    snackbarHostState: SnackbarHostState,
    product: Product.LocalViewModel,

    brandSuggestionsState: StateFlow<List<Brand>>,
    addProductState: StateFlow<State>,
    attributeSuggestionsState: StateFlow<List<Attribute>>,
    attributeValueSuggestionsState: StateFlow<List<AttributeValue>>,
    storeSuggestionsState: StateFlow<List<Store>>,
    shopSuggestionsState: StateFlow<List<Shop>>,
    productSuggestionsState: StateFlow<List<Product>>,
    measurementUnitSuggestionsState: StateFlow<List<MeasurementUnit>>,

    savePermanently: (Array<AddProductStep>) -> Unit,
    saveDraft: (Product.LocalViewModel) -> Unit,
    saveCurrentlyActiveStep: (AddProductStep) -> Unit,
    searchBrands: (Brand) -> Unit,
    searchAttributeValue: (AttributeValue) -> Unit,
    searchAttribute: (Attribute) -> Unit,
    searchStores: (Store) -> Unit,
    onStoreSearchSuggestionSelected: () -> Unit,
    searchShops: (Shop) -> Unit,
    onShopSearchSuggestionSelected: () -> Unit,
    searchProductNames: (ProductName) -> Unit,
    onProductSearchSuggestionSelected: () -> Unit,
    searchMeasurementUnits: (MeasurementUnit) -> Unit,
    onMeasurementUnitSearchSuggestionSelected: () -> Unit,
    onBrandSearchSuggestionSelected: (Brand) -> Unit,
    onAttributeValueSuggestionClicked: (AttributeValue) -> Unit,
    onAttributeSuggestionClicked: (Attribute) -> Unit,
) {
    val navigateToNext: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal + 1)
            .also(saveCurrentlyActiveStep)
    }

    val navigateToPrevious: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal - 1)
            .also(saveCurrentlyActiveStep)
    }

    val progress by remember(addProductStep) {
        derivedStateOf {
            (((addProductStep.ordinal + 1) * 1) / AddProductStep.values().size).toFloat()
        }
    }

    Column(modifier = Modifier.then(modifier)) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
        )
        AnimatedContent(targetState = addProductStep) { step ->
            when (step) {
                AddProductStep.Store -> {
                    val productDraft: (Store) -> Product.LocalViewModel by rememberUpdatedState {
                        product.copy(store = it.toLocalViewModel())
                    }
                    AddStorePage(
                        modifier = Modifier.fillMaxSize(),
                        store = product.store,
                        snackbarHostState = snackbarHostState,
                        suggestionsState = storeSuggestionsState,
                        permissionsState = locationPermissionsState,
                        search = searchStores,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onStoreSearchSuggestionSelected,
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
                        onPreviousClick = navigateToPrevious,
                        suggestionsState = shopSuggestionsState,
                        search = searchShops,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onShopSearchSuggestionSelected,
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
                        onPreviousClick = navigateToPrevious,
                        suggestionsState = productSuggestionsState,
                        search = searchProductNames,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onProductSearchSuggestionSelected,
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
                        suggestionsState = measurementUnitSuggestionsState,
                        search = searchMeasurementUnits,
                        onSuggestionSelected = {
                            productDraft(product.copy(measurementUnit = it.toLocalViewModel()))
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onMeasurementUnitSearchSuggestionSelected,
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
                        suggestionsState = brandSuggestionsState,
                        search = searchBrands,
                        onPreviousClick = navigateToPrevious,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onBrandSearchSuggestionSelected,
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
                        attributeValueSuggestionsState = attributeValueSuggestionsState,
                        attributeSuggestionsState = attributeSuggestionsState,
                        searchAttributeValue = searchAttributeValue,
                        onAttributeValueSuggestionClicked = onAttributeValueSuggestionClicked,
                        searchAttribute = searchAttribute,
                        onAttributeSuggestionClicked = onAttributeSuggestionClicked,
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
                        snackbarHostState = snackbarHostState,
                        stateState = addProductState,
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
        AddProductScreen(
            modifier = Modifier.fillMaxSize(),
            addProductStep = AddProductStep.valueOfOrdinalOrFirstByOrdinal(0),
            snackbarHostState = SnackbarHostState(),
            locationPermissionsState = LocationPermissionsState.Simulated,
            product = Product.LocalViewModel.default,

            brandSuggestionsState = MutableStateFlow(emptyList()),
            addProductState = MutableStateFlow(State.Idle),
            attributeSuggestionsState = MutableStateFlow(emptyList()),
            attributeValueSuggestionsState = MutableStateFlow(emptyList()),
            storeSuggestionsState = MutableStateFlow(emptyList()),
            shopSuggestionsState = MutableStateFlow(emptyList()),
            productSuggestionsState = MutableStateFlow(emptyList()),
            measurementUnitSuggestionsState = MutableStateFlow(emptyList()),

            savePermanently = {},
            saveDraft = {},
            saveCurrentlyActiveStep = {},
            searchBrands = {},
            searchAttributeValue = {},
            searchAttribute = {},
            searchStores = {},
            onStoreSearchSuggestionSelected = {},
            searchShops = {},
            onShopSearchSuggestionSelected = {},
            searchProductNames = {},
            onProductSearchSuggestionSelected = {},
            searchMeasurementUnits = {},
            onMeasurementUnitSearchSuggestionSelected = {},
            onBrandSearchSuggestionSelected = {},
            onAttributeValueSuggestionClicked = {},
            onAttributeSuggestionClicked = {},
        )
    }
}