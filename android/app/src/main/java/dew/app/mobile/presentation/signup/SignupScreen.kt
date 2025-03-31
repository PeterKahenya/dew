package dew.app.mobile.presentation.signup

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.LoginActivity
import dew.app.mobile.ProfileActivity
import dew.app.mobile.SignupActivity
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.model.UserSignup


@Composable
fun SignupScreen(
    viewModel: SignupViewModel
){
    val signupState by viewModel.state.collectAsStateWithLifecycle()
    val signup: MutableState<UserSignup> = remember {
        mutableStateOf(
            UserSignup(
                name = "", email = "", password = "", confirmPassword = ""
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
        ) {
            Text(
                text = "dew",
                color = Color.White,
                fontSize = 70.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 100.dp, bottom = 10.dp)
            )
            if (signupState.user != null) {
                println("SignupScreen signupResponse: ${signupState.user}")
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }

            if (signupState.error != null) {
                Text(text = signupState.error!!)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .padding(30.dp)
            ) {
                Text(
                    text = "Signup",
                    textAlign = TextAlign.Start,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
                if (signupState.isLoading) {
                    LinearProgressIndicator()
                }
                if (signupState.error != null) {
                    Text(
                        text = signupState.error!!,
                        color = Color.Red
                    )
                }
                OutlinedTextField(
                    value = signup.value.name,
                    onValueChange = {
                        signup.value = signup.value.copy(name = it)
                    },
                    placeholder = { Text(text = "Name") },
                    modifier = Modifier
                        .testTag("signupNameField")
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                OutlinedTextField(
                    value = signup.value.email,
                    onValueChange = {
                        signup.value = signup.value.copy(email = it)
                    },
                    placeholder = { Text(text = "Email") },
                    modifier = Modifier
                        .testTag("signupEmailField")
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                OutlinedTextField(
                    value = signup.value.password,
                    onValueChange = {
                        signup.value = signup.value.copy(password = it)
                    },
                    placeholder = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .testTag("signupPasswordField")
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                OutlinedTextField(
                    value = signup.value.confirmPassword,
                    onValueChange = {
                        signup.value = signup.value.copy(confirmPassword = it)
                    },
                    placeholder = { Text(text = "Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .testTag("signupConfirmPasswordField")
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                Button(
                    onClick = { viewModel.signup(signup.value) },
                    modifier = Modifier
                        .testTag("signupNextButton")
                        .padding(top = 25.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(250.dp)
                ) {
                    Text(text = "Signup")
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(text = "Already have an Account?", modifier = Modifier.align(Alignment.CenterVertically), color = Color.White)
                Button(
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                    modifier = Modifier
                        .testTag("signupLoginButton")
                        .width(100.dp)
                        .align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text(text = "Login", color = Color.DarkGray)
                }
            }

        }
    }

}