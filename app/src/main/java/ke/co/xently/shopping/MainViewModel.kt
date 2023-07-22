package ke.co.xently.shopping

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName
        private val CURRENT_ACTIVE_TAB_KEY = TAG.plus("CURRENT_ACTIVE_TAB_KEY")
    }

    val currentlyActiveTab = stateHandle.getStateFlow(
        CURRENT_ACTIVE_TAB_KEY,
        HomeTab.Recommendations,
    )

    fun saveCurrentlyActiveTab(tab: HomeTab) {
        stateHandle[CURRENT_ACTIVE_TAB_KEY] = tab
    }
}