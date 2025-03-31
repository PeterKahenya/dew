package dew.app.mobile.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.model.User
import dew.app.mobile.data.model.UserSignup
import dew.app.mobile.data.repository.AuthRepository
import dew.app.mobile.presentation.login.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


data class SignState(
    val isLoading: Boolean = false, val user: User? = null, val error: String? = null
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){

    private val _state = MutableStateFlow(SignState())
    val state: StateFlow<SignState> = _state.asStateFlow()

    fun signup(userSignup: UserSignup) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                val userResponse: User = authRepository.signup(userSignup)
                println("SignupResponse: $userResponse")
                _state.update {
                    it.copy(user = userResponse, isLoading = false, error = null)
                }
            } catch (e: HttpException) {
                val error = "Signup HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(user = null, isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Signup IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(user = null, isLoading = false, error = error)
                }
            } catch (e: SecurityException) {
                val error = "Signup SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(user = null, isLoading = false, error = error)
                }
            }
        }
    }
}