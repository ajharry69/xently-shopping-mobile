package ke.co.xently.features.core.ui.autocomplete

import androidx.compose.animation.core.CubicBezierEasing

internal object MotionTokens {
    const val DurationLong4 = 600.0
    const val DurationMedium3 = 350.0
    const val DurationShort2 = 100.0
    val EasingEmphasizedDecelerateCubicBezier = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
}