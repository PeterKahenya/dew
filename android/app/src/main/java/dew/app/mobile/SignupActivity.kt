package dew.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dew.app.mobile.presentation.login.LoginScreen
import dew.app.mobile.presentation.login.LoginViewModel
import dew.app.mobile.presentation.ui.theme.DewTheme

@AndroidEntryPoint
class TodayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewTheme {
                Text(text = "Today")
            }
        }
    }
}