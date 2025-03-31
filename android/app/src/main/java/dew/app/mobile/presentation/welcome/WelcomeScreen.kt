package dew.app.mobile.presentation.welcome

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.LoginActivity
import dew.app.mobile.TodayActivity

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel) {
    val authStatus by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF008080), // Start color
                        Color(0xFF1bc455)  // End color
                    )
                )
            )
    ) {
        if (authStatus.isLoading) {
            Text(text = "Loading...")
        }
        if (authStatus.isAuthenticated) {
            context.startActivity(
                Intent(
                    context,
                    TodayActivity::class.java
                )
            )
        } else {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 50.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    Text(
                        text = "dew",
                        color = Color.White,
                        fontSize = 100.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Your smart task assistant.", color = Color.White)
                }
                Button(
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text(text = "Login", color = Color.Black)
                }
            }
        }
    }
}