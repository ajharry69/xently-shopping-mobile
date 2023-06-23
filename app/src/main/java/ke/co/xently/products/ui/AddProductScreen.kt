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
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.models.Product
import ke.co.xently.products.models.Shop
import ke.co.xently.products.models.Store
import ke.co.xently.products.ui.subscreens.AddAttributesPage
import ke.co.xently.products.ui.subscreens.AddBrandsPage
import ke.co.xently.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.products.ui.subscreens.AddProductNamePage
import ke.co.xently.products.ui.subscreens.AddShopPage
import ke.co.xently.products.ui.subscreens.AddStorePage
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
        savePermanently = viewModel::savePermanently,
        saveDraft = viewModel::saveDraft,
        saveCurrentlyActiveStep = viewModel::saveCurrentlyActiveStep,
        modifier = modifier,
        product = product,
        brandSuggestionsState = viewModel.brandSuggestions,
        searchBrands = viewModel::searchBrand,
        addProductState = viewModel.addProductState,
        attributeSuggestionsState = viewModel.attributeSuggestions,
        searchAttribute = viewModel::searchAttribute,
        searchStores = viewModel::searchStore,
        searchShops = viewModel::searchShop,
        storeSuggestionsState = viewModel.storeSuggestions,
        shopSuggestionsState = viewModel.shopSuggestions,
        onStoreSearchSuggestionSelected = viewModel::clearStoreSearchSuggestions,
        onShopSearchSuggestionSelected = viewModel::clearShopSearchSuggestions,
    )
}

@Composable
fun AddProductScreen(
    addProductStep: AddProductStep,
    modifier: Modifier,
    product: Product.LocalViewModel,
    brandSuggestionsState: StateFlow<List<Brand>>,
    addProductState: StateFlow<State>,
    attributeSuggestionsState: StateFlow<List<AttributeValue>>,
    storeSuggestionsState: StateFlow<List<Store>>,
    shopSuggestionsState: StateFlow<List<Shop>>,
    savePermanently: (Product.LocalViewModel) -> Unit,
    saveDraft: (Product.LocalViewModel) -> Unit,
    saveCurrentlyActiveStep: (AddProductStep) -> Unit,
    searchBrands: (Brand) -> Unit,
    searchAttribute: (AttributeValue) -> Unit,
    searchStores: (Store) -> Unit,
    onStoreSearchSuggestionSelected: () -> Unit,
    searchShops: (Shop) -> Unit,
    onShopSearchSuggestionSelected: () -> Unit,
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
                    AddStorePage(
                        modifier = Modifier.fillMaxSize(),
                        store = product.store,
                        suggestionsState = storeSuggestionsState,
                        search = searchStores,
                        saveDraft = {
                            product.copy(store = it.toLocalViewModel())
                                .let(saveAsDraftOrPermanently)
                        },
                        onSearchSuggestionSelected = onStoreSearchSuggestionSelected,
                    ) {
                        product.copy(store = it.toLocalViewModel())
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
                }

                AddProductStep.Shop -> {
                    AddShopPage(
                        modifier = Modifier.fillMaxSize(),
                        shop = product.store.shop,
                        onPreviousClick = navigateToPrevious,
                        suggestionsState = shopSuggestionsState,
                        search = searchShops,
                        saveDraft = {
                            product.run {
                                copy(store = store.copy(shop = it.toLocalViewModel()))
                            }.let(saveAsDraftOrPermanently)
                        },
                        onSearchSuggestionSelected = onShopSearchSuggestionSelected,
                    ) {
                        product.copy(store = product.store.copy(shop = it.toLocalViewModel()))
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
                }

                AddProductStep.ProductName -> {
                    AddProductNamePage(
                        modifier = Modifier.fillMaxSize(),
                        productName = product.name,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        product.copy(name = it.toLocalViewModel())
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
                }

                AddProductStep.MeasurementUnit -> {
                    AddMeasurementUnitPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        it.toLocalViewModel()
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
                    AddBrandsPage(
                        modifier = Modifier.fillMaxSize(),
                        brands = product.brands,
                        suggestionsState = brandSuggestionsState,
                        search = searchBrands,
                        onPreviousClick = navigateToPrevious,
                        saveDraft = {
                            product.copy(brands = it.map(Brand::toLocalViewModel))
                                .let(saveAsDraftOrPermanently)
                        },
                    ) { brands ->
                        product.copy(brands = brands.map(Brand::toLocalViewModel))
                            .let(saveAsDraftOrPermanently)
                        navigateToNext()
                    }
                }

                AddProductStep.Attributes -> {
                    AddAttributesPage(
                        stateState = addProductState,
                        modifier = Modifier.fillMaxSize(),
                        attributes = product.attributes,
                        suggestionsState = attributeSuggestionsState,
                        search = searchAttribute,
                        onPreviousClick = navigateToPrevious,
                        saveDraft = {
                            product.copy(attributes = it.map(AttributeValue::toLocalViewModel))
                                .let(saveAsDraftOrPermanently)
                        },
                    ) { attributes ->
                        product.copy(attributes = attributes.map(AttributeValue::toLocalViewModel))
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
fun AddProductScreenPreview() {
    XentlyTheme {
        AddProductScreen(
            addProductStep = AddProductStep.valueOfOrdinalOrFirstByOrdinal(0),
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            brandSuggestionsState = MutableStateFlow(emptyList()),
            addProductState = MutableStateFlow(State.Idle),
            attributeSuggestionsState = MutableStateFlow(emptyList()),
            storeSuggestionsState = MutableStateFlow(emptyList()),
            shopSuggestionsState = MutableStateFlow(emptyList()),
            savePermanently = {},
            saveDraft = {},
            saveCurrentlyActiveStep = {},
            searchBrands = {},
            searchAttribute = {},
            searchStores = {},
            onStoreSearchSuggestionSelected = {},
            searchShops = {},
            onShopSearchSuggestionSelected = {},
        )
    }
}