package ke.co.xently.products.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.subscreens.AddMeasurementUnitScreen
import ke.co.xently.products.ui.subscreens.AddProductNameScreen

enum class AddProductStep {
    ProductName,
    MeasurementUnit;

    companion object {
        private val MAPPED_STAGES = values().groupBy {
            it.ordinal
        }.mapValues {
            it.value[0]
        }

        private fun valueOf(ordinal: Int): AddProductStep? {
            return MAPPED_STAGES[ordinal]
        }

        fun valueOfOrdinalOrFirstByOrdinal(ordinal: Int): AddProductStep {
            return valueOf(ordinal) ?: valueOf(0)!!
        }
    }
}

@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: ProductViewModel = hiltViewModel()) {
    var product by remember { mutableStateOf(Product.LocalViewModel.default) }
    var step by remember { mutableStateOf(AddProductStep.ProductName) }

    fun navigateToNextScreenAndPermanentlyPersistDataIfNecessary() {
        // Return to the first step after reaching the end but, before doing that, permanently persist the data
        if (step.ordinal == AddProductStep.values().lastIndex) {
            viewModel.savePermanently(product)
        } else {
            viewModel.saveDraft(product)
        }
        step = AddProductStep.valueOfOrdinalOrFirstByOrdinal(step.ordinal + 1)
    }

    when (step) {
        AddProductStep.ProductName -> {
            AddProductNameScreen(modifier = modifier) {
                product = product.copy(name = it.toLocalViewModel())
                navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
            }
        }

        AddProductStep.MeasurementUnit -> {
            AddMeasurementUnitScreen(modifier = modifier) {
                product = product.copy(measurementUnit = it?.toLocalViewModel())
                navigateToNextScreenAndPermanentlyPersistDataIfNecessary()
            }
        }
    }
}