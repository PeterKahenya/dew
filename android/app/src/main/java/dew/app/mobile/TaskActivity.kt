package dew.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
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


@Preview
@Composable
fun TodayPreview() {
    DewTheme {
        Text(text = "Today")
    }
}