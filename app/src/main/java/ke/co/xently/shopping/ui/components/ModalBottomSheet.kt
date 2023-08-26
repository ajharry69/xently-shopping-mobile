package ke.co.xently.shopping.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ke.co.xently.shopping.BottomSheet
import ke.co.xently.shopping.features.compareproducts.ui.CompareProductResponseScreen
import ke.co.xently.shopping.open
import kotlinx.coroutines.launch


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ModalBottomSheet(
    bottomSheet: () -> BottomSheet,
    hideBottomSheet: () -> Boolean,
) {
    var openBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val sheet = rememberUpdatedState(bottomSheet).value()

    LaunchedEffect(sheet) {
        openBottomSheet = sheet.open
    }

    val bottomSheetState = rememberModalBottomSheetState { sheetValue ->
        when (sheetValue) {
            SheetValue.Hidden -> hideBottomSheet()
            SheetValue.Expanded -> true
            SheetValue.PartiallyExpanded -> true
        }
    }

    val scope = rememberCoroutineScope()

    if (openBottomSheet) {
        // Example usage: https://www.composables.com/components/material3/modalbottomsheet
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                openBottomSheet = (!hideBottomSheet()).also {
                    if (it && !bottomSheetState.isVisible) {
                        scope.launch {
                            bottomSheetState.show()
                        }
                    }
                }
            },
        ) {
            when (sheet) {
                is BottomSheet.Ignore -> {

                }

                is BottomSheet.CompareProductResponse -> {
                    CompareProductResponseScreen(
                        modifier = Modifier,
                        response = sheet.data,
                    )
                }
            }
            AnimatedVisibility(openBottomSheet) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}