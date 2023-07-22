package ke.co.xently.features.recommendations.ui

import ke.co.xently.R
import ke.co.xently.features.core.ui.UIState

internal sealed class RecommendationRequestUIState(message: Int) : UIState(message) {
    sealed class NameError(message: Int) : RecommendationRequestUIState(message) {
        object ImojisNotAllowed : NameError(R.string.xently_error_imojis_not_allowed)
    }

    object OK : RecommendationRequestUIState(android.R.string.ok)
}