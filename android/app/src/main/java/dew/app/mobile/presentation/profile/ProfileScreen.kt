package dew.app.mobile.presentation.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.TodayActivity
import dew.app.mobile.data.model.Auth

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel
) {
    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (profileState.auth != null) {
            ProfileContent(profileState.auth!!, profileViewModel)
        }
        if (profileState.isLoading) {
            Text("Loading...")
        }
        if (profileState.error != null) {
            Text(profileState.error!!)
        }
    }
}

@Composable
fun ProfileContent(profile: Auth, profileViewModel: ProfileViewModel) {
    val name = remember { mutableStateOf(profile.name) }
    val context = LocalContext.current
    Avatar(profile)
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = profile.name, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = profile.email)
    Spacer(modifier = Modifier.height(200.dp))
    Button(onClick = {
        val intent = Intent(context, TodayActivity::class.java)
        context.startActivity(intent)
    },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ){
        Text("Go to Tasks")
    }
}

@Composable
fun Avatar(profile: Auth) {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = CircleShape,
        color = Color.LightGray
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = profile.name[0].toString().uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}