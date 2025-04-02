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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
                        MaterialTheme.colorScheme.primary, // Start color
                        MaterialTheme.colorScheme.secondary  // End color
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
                text = "Dew",
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary, fontSize = 70.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
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
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(30.dp)
            ) {
                Text(
                    text = "SIGNUP",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (signupState.isLoading) {
                    LinearProgressIndicator()
                }
                if (signupState.error != null) {
                    Text(
                        text = signupState.error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                OutlinedTextField(
                    value = signup.value.name,
                    onValueChange = {
                        signup.value = signup.value.copy(name = it)
                    },
                    placeholder = { Text(text = "Name", color = MaterialTheme.colorScheme.onPrimary) },
                    modifier = Modifier
                        .testTag("signupNameField")
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
                )
                OutlinedTextField(
                    value = signup.value.email,
                    onValueChange = {
                        signup.value = signup.value.copy(email = it)
                    },
                    placeholder = { Text(text = "Email",color = MaterialTheme.colorScheme.onPrimary) },
                    modifier = Modifier
                        .testTag("signupEmailField")
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
                )
                OutlinedTextField(
                    value = signup.value.password,
                    onValueChange = {
                        signup.value = signup.value.copy(password = it)
                    },
                    placeholder = { Text(text = "Password",color = MaterialTheme.colorScheme.onPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .testTag("signupPasswordField")
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
                )
                OutlinedTextField(
                    value = signup.value.confirmPassword,
                    onValueChange = {
                        signup.value = signup.value.copy(confirmPassword = it)
                    },
                    placeholder = { Text(text = "Confirm Password", color = MaterialTheme.colorScheme.onPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .testTag("signupConfirmPasswordField")
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(text = "Login", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

        }
    }

}