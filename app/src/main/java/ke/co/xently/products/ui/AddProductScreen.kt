package ke.co.xently.products.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.subscreens.AddAttributesPage
import ke.co.xently.products.ui.subscreens.AddBrandsPage
import ke.co.xently.products.ui.subscreens.AddGeneralDetailsPage
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.products.ui.subscreens.AddProductNamePage
import ke.co.xently.products.ui.subscreens.AddStorePage

@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    var product by remember { mutableStateOf(Product.LocalViewModel.default) }
    var addProductStep by remember {
        mutableStateOf(AddProductStep.valueOfOrdinalOrFirstByOrdinal(0))
    }

    fun navigateToNextScreenAndPermanentlyPersistDataIfNecessary() {
        // Return to the first step after reaching the end but, before doing that, permanently persist the data
        if (addProductStep.ordinal == AddProductStep.values().lastIndex) {
            viewModel.savePermanently(product)
        } else {
            viewModel.saveDraft(product)
        }
        addProductStep = AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal + 1)
    }

    val navigateToPrevious: () -> Unit by rememberUpdatedState(
        newValue = {
            addProductStep =
                AddProductStep.valueOfOrdinalOrFirstByOrdinal(addProductStep.ordinal - 1)
        },
    )

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
                        product = product.copy(store = it.toLocalViewModel())
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }

                AddProductStep.ProductName -> {
                    AddProductNamePage(
                        modifier = Modifier.fillMaxSize(),
                        productName = product.name,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        product = product.copy(name = it.toLocalViewModel())
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }

                AddProductStep.MeasurementUnit -> {
                    AddMeasurementUnitPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        product = it.toLocalViewModel()
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }

                AddProductStep.GeneralDetails -> {
                    AddGeneralDetailsPage(
                        modifier = Modifier.fillMaxSize(),
                        product = product,
                        onPreviousClick = navigateToPrevious,
                    ) {
                        product = it.toLocalViewModel()
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }

                AddProductStep.Brands -> {
                    AddBrandsPage(
                        modifier = Modifier.fillMaxSize(),
                        brands = product.brands,
                        onPreviousClick = navigateToPrevious,
                    ) { brands ->
                        product = product.copy(brands = brands.map { it.toLocalViewModel() })
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }

                AddProductStep.Attributes -> {
                    AddAttributesPage(
                        modifier = Modifier.fillMaxSize(),
                        attributes = product.attributes,
                        onPreviousClick = navigateToPrevious,
                    ) { attributes ->
                        product =
                            product.copy(attributes = attributes.map { it.toLocalViewModel() })
                        navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
                    }
                }
            }
        }
    }
}