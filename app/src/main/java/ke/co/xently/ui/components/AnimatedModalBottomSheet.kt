package ke.co.xently.ui.components

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
import ke.co.xently.BottomSheet
import ke.co.xently.features.compareproducts.ui.CompareProductResponseScreen
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.features.recommendations.ui.RecommendationResponseDetailsScreen
import ke.co.xently.features.recommendations.ui.RecommendationResponseScreen
import ke.co.xently.open
import kotlinx.coroutines.launch


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AnimatedModalBottomSheet(
    bottomSheet: () -> BottomSheet,
    navigateToStore: (Recommendation.Response) -> Unit,
    visitOnlineStore: (Recommendation.Response) -> Unit,
    updateBottomSheet: (BottomSheet) -> Unit,
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

    AnimatedVisibility(openBottomSheet) {
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

                is BottomSheet.RecommendationResponse.Many -> {
                    RecommendationResponseScreen(
                        modifier = Modifier,
                        response = sheet.data,
                        onNavigate = navigateToStore,
                        visitOnlineStore = visitOnlineStore,
                        onViewProduct = {
                            updateBottomSheet(BottomSheet.RecommendationResponse.Single(it))
                        },
                    )
                }

                is BottomSheet.RecommendationResponse.Single -> {
                    RecommendationResponseDetailsScreen(
                        modifier = Modifier,
                        response = sheet.data,
                        onNavigate = navigateToStore,
                        visitOnlineStore = visitOnlineStore,
                    )
                }
            }
            AnimatedVisibility(openBottomSheet) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}