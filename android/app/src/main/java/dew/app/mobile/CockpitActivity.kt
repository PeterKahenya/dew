package dew.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dew.app.mobile.presentation.cockpit.CockpitScreen
import dew.app.mobile.presentation.ui.theme.DewTheme

@AndroidEntryPoint
class CockpitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DewTheme {
                CockpitScreen()
            }
        }
    }
}