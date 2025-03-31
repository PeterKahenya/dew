package dew.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dew.app.mobile.presentation.signup.SignupScreen
import dew.app.mobile.presentation.signup.SignupViewModel
import dew.app.mobile.presentation.ui.theme.DewTheme

@AndroidEntryPoint
class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewTheme {
                val viewModel = hiltViewModel<SignupViewModel>()
                SignupScreen(viewModel)
            }
        }
    }
}