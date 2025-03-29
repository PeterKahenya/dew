package dew.app.mobile.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.model.Auth
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false, val auth: Auth? = null, val error: String? = null
)


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(userLogin: UserLogin) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                val loginAuth: Auth = authRepository.login(userLogin)
                println("LoginResponse: $loginAuth")
                _state.update {
                    it.copy(auth = loginAuth, isLoading = false, error = null)
                }
            } catch (e: HttpException) {
                val error = "Login HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Login IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            } catch (e: SecurityException) {
                val error = "Login SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            }
        }
    }
}