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
    )
}

@Composable
fun AddProductScreen(
    addProductStep: AddProductStep,
    savePermanently: (product: Product.LocalViewModel) -> Unit,
    saveDraft: (product: Product.LocalViewModel) -> Unit,
    saveCurrentlyActiveStep: (step: AddProductStep) -> Unit,
    modifier: Modifier,
    product: Product.LocalViewModel,
    brandSuggestionsState: StateFlow<List<Brand>>,
    searchBrands: (brand: Brand) -> Unit,
    addProductState: StateFlow<State>,
    attributeSuggestionsState: StateFlow<List<AttributeValue>>,
    searchAttribute: (attribute: AttributeValue) -> Unit,
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
                    AddStorePage(modifier = Modifier.fillMaxSize(), store = product.store) {
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
            savePermanently = {},
            saveDraft = {},
            saveCurrentlyActiveStep = {},
            modifier = Modifier.fillMaxSize(),
            product = Product.LocalViewModel.default,
            brandSuggestionsState = MutableStateFlow(emptyList()),
            searchBrands = {},
            addProductState = MutableStateFlow(State.Idle),
            attributeSuggestionsState = MutableStateFlow(emptyList()),
            searchAttribute = {},
        )
    }
}