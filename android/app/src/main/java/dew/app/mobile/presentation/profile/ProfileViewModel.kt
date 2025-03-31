package dew.app.mobile.presentation.profile

import dew.app.mobile.data.model.Auth
import dew.app.mobile.data.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val auth: Auth? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel(){

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            try {
                _profileState.update {
                    it.copy(isLoading = true)
                }
                val auth: Auth? = authRepository.auth()
                if (auth != null) {
                    _profileState.update {
                        it.copy(auth = auth, isLoading = false, error = null)
                    }
                }else{
                    _profileState.update {
                        it.copy(auth = null, isLoading = false, error = "No auth found")
                    }
                }
            } catch (e: HttpException) {
                val error = "GetProfile HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _profileState.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error = "GetProfile IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _profileState.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            } catch (e: java.lang.SecurityException) {
                val error = "GetProfile SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _profileState.update {
                    it.copy(auth = null, isLoading = false, error = error)
                }
            }
        }
    }
}