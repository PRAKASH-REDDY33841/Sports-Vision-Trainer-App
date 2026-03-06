package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*

@Composable
fun ResetPasswordScreen(nav: NavController, email: String) {

    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    // ✅ password match check
    val match = pass.isNotEmpty() && pass == confirm

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        // ✅ CENTERED COLUMN
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // 🔙 Back arrow
            IconButton(onClick = { nav.navigate("login") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Set New Password",
                fontSize = 22.sp
            )

            Spacer(Modifier.height(24.dp))

            // 🔒 New password
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("New Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 🔒 Confirm password with tick when match
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Re-enter Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    if (match) {
                        Icon(Icons.Default.Done, contentDescription = "Match")
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // ✅ Reset button — enabled only when match
            Button(
                enabled = match,
                onClick = {

                    RetrofitClient.api.resetPasswordFinal(
                        ResetRequest(email, pass)
                    ).enqueue(object : retrofit2.Callback<ApiResponse> {

                        override fun onResponse(
                            call: retrofit2.Call<ApiResponse>,
                            response: retrofit2.Response<ApiResponse>
                        ) {
                            if (response.body()?.status == "success") {
                                nav.navigate("login")
                            } else {
                                msg = "Failed"
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<ApiResponse>,
                            t: Throwable
                        ) {
                            msg = "Server error"
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Reset Password")
            }

            Spacer(Modifier.height(12.dp))

            if (msg.isNotEmpty()) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
