package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(nav: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var eTouched by remember { mutableStateOf(false) }
    var eBlur by remember { mutableStateOf(false) }

    var pTouched by remember { mutableStateOf(false) }
    var pBlur by remember { mutableStateOf(false) }

    var submitError by remember { mutableStateOf(false) }
    var wrongCred by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Spacer(Modifier.height(60.dp))

        // ✅ TITLE
        Text(
            "VISION TRAINING FOR ATHLETES",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            // ✅ EMAIL FIELD
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    wrongCred = false
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = eTouched && eBlur && email.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) eTouched = true
                        else if (eTouched) eBlur = true
                    }
            )

            if (eTouched && eBlur && email.isBlank())
                Text(
                    "* Fill this field",
                    color = MaterialTheme.colorScheme.error
                )

            Spacer(Modifier.height(12.dp))

            // ✅ PASSWORD FIELD WITH VISIBILITY TOGGLE
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    wrongCred = false
                },
                label = { Text("Password") },
                visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector =
                            if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                isError = pTouched && pBlur && password.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) pTouched = true
                        else if (pTouched) pBlur = true
                    }
            )

            if (pTouched && pBlur && password.isBlank())
                Text(
                    "* Password required",
                    color = MaterialTheme.colorScheme.error
                )

            Spacer(Modifier.height(6.dp))

            TextButton(
                onClick = { nav.navigate("forgot") }
            ) {
                Text("Forgot Password?")
            }

            Spacer(Modifier.height(12.dp))

            // ✅ LOGIN BUTTON
            Button(
                onClick = {

                    submitError = false

                    if (email.isBlank() || password.isBlank()) {
                        submitError = true
                        return@Button
                    }

                    RetrofitClient.api.login(
                        LoginRequest(email, password)
                    ).enqueue(object : Callback<ApiResponse> {

                        override fun onResponse(
                            call: Call<ApiResponse>,
                            r: Response<ApiResponse>
                        ) {
                            if (r.body()?.status == "success") {

                                val name =
                                    r.body()?.username
                                        ?: email.substringBefore("@")

                                nav.navigate("home/$email/$name") {
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {
                                wrongCred = true
                            }
                        }

                        override fun onFailure(
                            call: Call<ApiResponse>,
                            t: Throwable
                        ) {
                            wrongCred = true
                        }
                    })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            if (submitError)
                Text(
                    "* Must fill above details",
                    color = MaterialTheme.colorScheme.error
                )

            if (wrongCred)
                Text(
                    "* User not registered or wrong password",
                    color = MaterialTheme.colorScheme.error
                )

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { nav.navigate("register") }
            ) {
                Text("Register now")
            }
        }
    }
}
