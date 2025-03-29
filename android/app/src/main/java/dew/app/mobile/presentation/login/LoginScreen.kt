package dew.app.mobile.presentation.login

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.ProfileActivity
import dew.app.mobile.data.model.UserLogin

@OptIn(ExperimentalFoundationApi::class)
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

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(30.dp)
            .padding(top = 50.dp)
    ) {
        TextField(
            value = login.value.email,
            onValueChange = {
                login.value = login.value.copy(email = it)
            },
            label = { Text(text = "Email") },
            modifier = Modifier.testTag("loginEmailField").fillMaxWidth()
        )
        TextField(
            value = login.value.password,
            onValueChange = {
                login.value = login.value.copy(password = it)
            },
            label = { Text(text = "Password") },
            modifier = Modifier.testTag("loginPasswordField").fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (loginState.isLoading) {
            Text(text = "Loading...")
        }
        if (loginState.error != null) {
            Text(text = loginState.error!!)
        }
        Button(
            onClick = { viewModel.login(login.value) },
            modifier = Modifier
                .testTag("loginNextButton")
                .padding(top = 100.dp)
                .align(Alignment.End)
                .width(150.dp),
        ) {
            Text(text = "Login")
        }
    }

    if (loginState.auth != null) {
        println("LoginScreen loginResponse: ${loginState.auth}")
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }


}