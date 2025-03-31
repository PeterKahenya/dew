package dew.app.mobile.presentation.login

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.ProfileActivity
import dew.app.mobile.SignupActivity
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.repository.AuthRepository
import dew.app.mobile.data.repository.AuthRepositoryImpl

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val loginState by viewModel.state.collectAsStateWithLifecycle()
    val login: MutableState<UserLogin> = remember {
        mutableStateOf(
            UserLogin(
                email = "", password = ""
            )
        )
    }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        )  {
            Text(
                text = "dew",
                color = Color.White,
                fontSize = 70.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 100.dp, bottom = 10.dp)
            )
            if (loginState.auth != null) {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(400.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .padding(30.dp)
            ) {
                Text(
                    text = "Login",
                    textAlign = TextAlign.Start,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
                if (loginState.isLoading) {
                    LinearProgressIndicator()
                }
                if (loginState.error != null) {
                    Text(
                        text = loginState.error!!,
                        color = Color.Red
                    )
                }
                OutlinedTextField(
                    value = login.value.email,
                    enabled = !loginState.isLoading,
                    onValueChange = {
                        login.value = login.value.copy(email = it)
                    },
                    placeholder={ Text(text = "Email")},
                    modifier = Modifier
                        .testTag("loginEmailField")
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )
                OutlinedTextField(
                    value = login.value.password,
                    enabled = !loginState.isLoading,
                    onValueChange = {
                        login.value = login.value.copy(password = it)
                    },
                    placeholder={ Text(text = "Password")},
                    modifier = Modifier
                        .testTag("loginPasswordField")
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Button(
                    onClick = { viewModel.login(login.value) },
                    enabled = !loginState.isLoading,
                    modifier = Modifier
                        .testTag("loginNextButton")
                        .padding(top = 35.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(250.dp),
                ) {
                    Text(text = "Login")
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(text = "Don't have an account?", modifier = Modifier.align(Alignment.CenterVertically), color = Color.White)
                Button(
                    onClick = {
                        context.startActivity(Intent(context, SignupActivity::class.java))
                    },
                    modifier = Modifier
                        .testTag("loginNextButton")
                        .width(100.dp)
                        .align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text(text = "Signup", color = Color.DarkGray)
                }
            }
        }
    }

}