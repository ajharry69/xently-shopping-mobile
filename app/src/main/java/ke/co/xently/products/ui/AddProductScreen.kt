package ke.co.xently.products.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitPage
import ke.co.xently.products.ui.subscreens.AddProductNamePage
import ke.co.xently.products.ui.subscreens.AddStorePage

@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: ProductViewModel = hiltViewModel()) {
    var product by remember { mutableStateOf(Product.LocalViewModel.default) }
    var step by remember { mutableStateOf(AddProductStep.valueOfOrdinalOrFirstByOrdinal(0)) }

    fun navigateToNextScreenAndPermanentlyPersistDataIfNecessary() {
        // Return to the first step after reaching the end but, before doing that, permanently persist the data
        if (step.ordinal == AddProductStep.values().lastIndex) {
            viewModel.savePermanently(product)
        } else {
            viewModel.saveDraft(product)
        }
        step = AddProductStep.valueOfOrdinalOrFirstByOrdinal(step.ordinal + 1)
    }

    val navigateToPrevious: () -> Unit by rememberUpdatedState(
        newValue = {
            step = AddProductStep.valueOfOrdinalOrFirstByOrdinal(step.ordinal - 1)
        },
    )

    when (step) {
        AddProductStep.Store -> {
            AddStorePage(modifier = modifier) {
                product = product.copy(store = it.toLocalViewModel())
                navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
            }
        }

        AddProductStep.ProductName -> {
            AddProductNamePage(
                modifier = modifier,
                productName = product.name,
                onPreviousClick = navigateToPrevious,
            ) {
                product = product.copy(name = it.toLocalViewModel())
                navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
            }
        }

        AddProductStep.MeasurementUnit -> {
            AddMeasurementUnitPage(
                modifier = modifier,
                measurementUnit = product.measurementUnit,
                onPreviousClick = navigateToPrevious,
            ) {
                product = product.copy(measurementUnit = it?.toLocalViewModel())
                navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
            }
        }
    }
}