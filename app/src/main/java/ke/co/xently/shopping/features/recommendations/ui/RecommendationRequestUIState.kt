package ke.co.xently.shopping.features.recommendations.ui

import ke.co.xently.shopping.R
import ke.co.xently.shopping.features.core.ui.UIState

internal sealed class RecommendationRequestUIState(message: Int) : UIState(message) {
    object BlankNameNotAllowed :
        RecommendationRequestUIState(R.string.xently_error_blank_not_allowed)
    sealed class NameError(message: Int) : RecommendationRequestUIState(message) {
        object ImojisNotAllowed : NameError(R.string.xently_error_imojis_not_allowed)
    }

    object OK : RecommendationRequestUIState(android.R.string.ok)
}