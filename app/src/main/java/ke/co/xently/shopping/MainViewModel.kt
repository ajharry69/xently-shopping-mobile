package ke.co.xently.shopping

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.xently.shopping.features.authentication.repositories.AuthenticationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName
        private val CURRENT_ACTIVE_TAB_KEY = TAG.plus("CURRENT_ACTIVE_TAB_KEY")
    }

    val currentlyActiveTab = stateHandle.getStateFlow(
        CURRENT_ACTIVE_TAB_KEY,
        HomeTab.Recommendations,
    )

    val currentlySignInUser = authenticationRepository.getCurrentlySignedInUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun saveCurrentlyActiveTab(tab: HomeTab) {
        stateHandle[CURRENT_ACTIVE_TAB_KEY] = tab
    }
}