package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sports_vision_trainer.R
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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(15.dp))

        // 🔹 TOP LOGOS IN ONE ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = "https://simatscgpa.netlify.app/logo2.png",
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.vision_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(140.dp)

            )

            AsyncImage(
                model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQzSASJ8CW7h0pmb79FrMdRMp73kQ96SnFPg&s",
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // TITLE
        Text(
            "VISION TRAINING FOR ATHLETES",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            // EMAIL FIELD
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

            // PASSWORD FIELD
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

            // LOGIN BUTTON
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

        Spacer(Modifier.weight(1f))

        // FOOTER
        Text(
            "Powered by SIMATS Engineering",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(10.dp))
    }
}