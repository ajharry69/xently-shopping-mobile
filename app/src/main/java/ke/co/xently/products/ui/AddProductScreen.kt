package ke.co.xently.products.ui

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
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.products.models.AttributeValue
import ke.co.xently.products.models.Brand
import ke.co.xently.products.ui.subscreens.AddAttributesPage
import ke.co.xently.products.ui.subscreens.AddBrandsPage
import ke.co.xently.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.products.ui.subscreens.AddProductNamePage
import ke.co.xently.products.ui.subscreens.AddShopPage
import ke.co.xently.products.ui.subscreens.AddStorePage

@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    val addProductStep by viewModel.currentlyActiveStep.collectAsState()
    val product by viewModel.product.collectAsState()

    val isTheLastStep by remember(addProductStep) {
        derivedStateOf {
            addProductStep.ordinal == AddProductStep.values().lastIndex
        }
    }

    val navigateToNext: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal + 1)
            .also(viewModel::saveCurrentlyActiveStep)
    }

    val navigateToPrevious: () -> Unit by rememberUpdatedState {
        AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal - 1)
            .also(viewModel::saveCurrentlyActiveStep)
    }

    val progress by remember(addProductStep) {
        derivedStateOf {
            (((addProductStep.ordinal + 1) * 1) / AddProductStep.values().size).toFloat()
        }
    }

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
        )
        AnimatedContent(targetState = addProductStep) { step ->
            when (step) {
                AddProductStep.Store -> {
                    AddStorePage(modifier = Modifier.fillMaxSize(), store = product.store) {
                        product.copy(store = it.toLocalViewModel())
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
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
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
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
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
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
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
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
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Brands -> {
                    AddBrandsPage(
                        modifier = Modifier.fillMaxSize(),
                        brands = product.brands,
                        suggestionsState = viewModel.brandSuggestions,
                        search = viewModel::searchBrand,
                        onPreviousClick = navigateToPrevious,
                        saveDraft = {
                            product.copy(brands = it.map(Brand::toLocalViewModel))
                                .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
                        },
                    ) { brands ->
                        product.copy(brands = brands.map(Brand::toLocalViewModel))
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
                        navigateToNext()
                    }
                }

                AddProductStep.Attributes -> {
                    AddAttributesPage(
                        stateState = viewModel.addProductState,
                        modifier = Modifier.fillMaxSize(),
                        attributes = product.attributes,
                        suggestionsState = viewModel.attributeSuggestions,
                        search = viewModel::searchAttribute,
                        onPreviousClick = navigateToPrevious,
                        saveDraft = {
                            product.copy(attributes = it.map(AttributeValue::toLocalViewModel))
                                .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
                        },
                    ) { attributes ->
                        product.copy(attributes = attributes.map(AttributeValue::toLocalViewModel))
                            .let(if (isTheLastStep) viewModel::savePermanently else viewModel::saveDraft)
                        navigateToNext()
                    }
                }
            }
        }
    }
}