package dew.app.mobile.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(AuthState())
    val isAuthenticated: StateFlow<AuthState> = _isAuthenticated.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth(){
        viewModelScope.launch {
            try {
                _isAuthenticated.update {
                    it.copy(isLoading = true, isAuthenticated = false, error = null)
                }
                val auth = authRepository.auth()
                if (auth != null) {
                    _isAuthenticated.update {
                        it.copy(isAuthenticated = true, isLoading = false, error = null)
                    }
                } else {
                    println("checkAuth: auth is null, you may want to redirect to login screen")
                    _isAuthenticated.update {
                        it.copy(isAuthenticated = false, isLoading = false, error = null)
                    }
                }
            } catch (e: Exception) {
                println("WelcomeViewModel checkAuth: exception: ${e.message}")
                _isAuthenticated.update {
                    it.copy(isAuthenticated = false, isLoading = false, error = e.localizedMessage)
                }
            }
        }
    }
}