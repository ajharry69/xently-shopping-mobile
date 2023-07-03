package ke.co.xently

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private val CURRENT_ACTIVE_TAB_KEY =
            MainActivity::class.java.simpleName.plus("CURRENT_ACTIVE_TAB_KEY")
    }

    val currentlyActiveTab = stateHandle.getStateFlow(
        CURRENT_ACTIVE_TAB_KEY,
        HomeTab.Recommendations,
    )

    fun saveCurrentlyActiveTab(tab: HomeTab) {
        stateHandle[CURRENT_ACTIVE_TAB_KEY] = tab
    }
}