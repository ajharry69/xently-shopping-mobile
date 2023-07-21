package ke.co.xently.features.measurementunit.ui

import androidx.compose.runtime.staticCompositionLocalOf
import ke.co.xently.features.measurementunit.datasources.remoteservices.MeasurementUnitAutoCompleteService

val LocalMeasurementUnitAutoCompleteService =
    staticCompositionLocalOf<MeasurementUnitAutoCompleteService> {
        MeasurementUnitAutoCompleteService.Fake
    }