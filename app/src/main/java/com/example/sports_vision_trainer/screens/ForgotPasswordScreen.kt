package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*

@Composable
fun ForgotPasswordScreen(nav: NavController) {

    var email by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(Modifier.height(12.dp))

            // 🔙 Back Arrow (NOT centered — top left)
            IconButton(
                onClick = { nav.navigate("login") },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            // ✅ Center everything below
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // 🧾 Title
                Text(
                    text = "Forgot Password",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                // 📄 Subtitle
                Text(
                    text = "Enter your email to reset your password",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(32.dp))

                // 📧 Label
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                // 📩 Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("user@gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(32.dp))

                // 🚀 Submit Button (same color as Login button)
                Button(
                    onClick = {

                        RetrofitClient.api.sendOtp(
                            ForgotRequest(email)
                        ).enqueue(object : retrofit2.Callback<ApiResponse> {

                            override fun onResponse(
                                call: retrofit2.Call<ApiResponse>,
                                response: retrofit2.Response<ApiResponse>
                            ) {
                                if (response.body()?.status == "success") {
                                    nav.navigate("otp/$email")
                                } else {
                                    msg = "Email not found"
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
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Submit", fontSize = 16.sp)
                }

                Spacer(Modifier.height(16.dp))

                // ⚠️ Message
                if (msg.isNotEmpty()) {
                    Text(
                        msg,
                        color = Color.Red
                    )
                }
            }
        }
    }
}
