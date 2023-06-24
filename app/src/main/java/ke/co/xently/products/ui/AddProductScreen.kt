package ke.co.xently.products.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.products.models.Attribute
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.MeasurementUnit
import ke.co.xently.products.models.Product
import ke.co.xently.products.models.ProductName
import ke.co.xently.products.models.Shop
import ke.co.xently.products.models.Store
import ke.co.xently.products.ui.subscreens.AddAttributesPage
import ke.co.xently.products.ui.subscreens.AddBrandsPage
import ke.co.xently.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.products.ui.subscreens.AddProductNamePage
import ke.co.xently.products.ui.subscreens.AddShopPage
import ke.co.xently.products.ui.subscreens.AddStorePage
import ke.co.xently.products.ui.subscreens.SummaryPage
import ke.co.xently.ui.theme.XentlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddProductScreen(
    modifier: Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    val addProductStep: AddProductStep by viewModel.currentlyActiveStep.collectAsState()
    val product: Product.LocalViewModel by viewModel.product.collectAsState()

    AddProductScreen(
        addProductStep = addProductStep,
        modifier = modifier,
        product = product,
        brandSuggestionsState = viewModel.brandSuggestions,
        addProductState = viewModel.addProductState,
        attributeSuggestionsState = viewModel.attributeSuggestions,
        attributeValueSuggestionsState = viewModel.attributeValueSuggestions,
        storeSuggestionsState = viewModel.storeSuggestions,
        shopSuggestionsState = viewModel.shopSuggestions,
        productSuggestionsState = viewModel.productSuggestions,
        measurementUnitSuggestionsState = viewModel.measurementUnitSuggestions,
        savePermanently = viewModel::savePermanently,
        saveDraft = viewModel::saveDraft,
        saveCurrentlyActiveStep = viewModel::saveCurrentlyActiveStep,
        searchBrands = viewModel::searchBrand,
        searchAttribute = viewModel::searchAttribute,
        searchAttributeValue = viewModel::searchAttributeValue,
        searchStores = viewModel::searchStore,
        onStoreSearchSuggestionSelected = viewModel::clearStoreSearchSuggestions,
        searchShops = viewModel::searchShop,
        onShopSearchSuggestionSelected = viewModel::clearShopSearchSuggestions,
        searchProductNames = viewModel::searchProductName,
        onProductSearchSuggestionSelected = viewModel::clearProductSearchSuggestions,
        searchMeasurementUnits = viewModel::searchMeasurementUnit,
        onMeasurementUnitSearchSuggestionSelected = viewModel::clearMeasurementUnitSearchSuggestions,
        onBrandSearchSuggestionSelected = { viewModel.clearBrandSearchSuggestions() },
        onAttributeValueSuggestionClicked = { viewModel.clearAttributeValueSuggestions() },
        onAttributeSuggestionClicked = { viewModel.clearAttributeSuggestions() },
    )
}

@Composable
fun AddProductScreen(
    addProductStep: AddProductStep,
    modifier: Modifier,
    product: Product.LocalViewModel,
    brandSuggestionsState: StateFlow<List<Brand>>,
    addProductState: StateFlow<State>,
    attributeSuggestionsState: StateFlow<List<Attribute>>,
    attributeValueSuggestionsState: StateFlow<List<AttributeValue>>,
    storeSuggestionsState: StateFlow<List<Store>>,
    shopSuggestionsState: StateFlow<List<Shop>>,
    productSuggestionsState: StateFlow<List<Product>>,
    savePermanently: (Product.LocalViewModel) -> Unit,
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
    measurementUnitSuggestionsState: StateFlow<List<MeasurementUnit>>,
    searchMeasurementUnits: (MeasurementUnit) -> Unit,
    onMeasurementUnitSearchSuggestionSelected: () -> Unit,
    onBrandSearchSuggestionSelected: (Brand) -> Unit,
    onAttributeValueSuggestionClicked: (AttributeValue) -> Unit,
    onAttributeSuggestionClicked: (Attribute) -> Unit,
) {
    val saveAsDraftOrPermanently: (Product.LocalViewModel) -> Unit by remember(addProductStep) {
        derivedStateOf {
            if (addProductStep.ordinal == AddProductStep.values().lastIndex) {
                savePermanently
            } else {
                saveDraft
            }
        }
    }

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
                        suggestionsState = storeSuggestionsState,
                        search = searchStores,
                        saveDraft = {
                            productDraft(it)
                                .let(saveDraft)
                        },
                        onSearchSuggestionSelected = onStoreSearchSuggestionSelected,
                    ) {
                        productDraft(it)
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
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
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
                }

                AddProductStep.Summary -> {
                    SummaryPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        stateState = addProductState,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        it.toLocalViewModel()
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
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
            addProductStep = AddProductStep.valueOfOrdinalOrFirstByOrdinal(0),
            modifier = Modifier.fillMaxSize(),
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
            searchAttribute = {},
            searchAttributeValue = {},
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