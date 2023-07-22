package ke.co.xently.shopping.features.measurementunit.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.shopping.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService

val LocalMeasurementUnitAutoCompleteService =
    staticCompositionLocalOf<MeasurementUnitAutoCompleteService> {
        MeasurementUnitAutoCompleteService.Fake
    }