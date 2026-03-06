package com.example.sports_vision_trainer.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(nav: NavController, email: String) {

    val digits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focus = List(6) { remember { FocusRequester() } }

    var msg by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf(30) }
    var shakeTrigger by remember { mutableStateOf(0) }

    // ✅ resend timer loop
    LaunchedEffect(seconds) {
        if (seconds > 0) {
            delay(1000)
            seconds--
        }
    }

    // ✅ auto submit when filled
    LaunchedEffect(digits.joinToString("")) {
        if (digits.all { it.isNotEmpty() }) {
            verifyOtp(
                nav,
                email,
                digits.joinToString(""),
                onFail = {
                    msg = "Invalid OTP"
                    shakeTrigger++
                    for (i in 0..5) digits[i] = ""
                    focus[0].requestFocus()
                }
            )
        }
    }

    val shake by animateFloatAsState(
        targetValue = if (shakeTrigger % 2 == 0) 0f else 1f,
        animationSpec = tween(350),
        label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column {

            Spacer(Modifier.height(12.dp))

            // 🔙 Back
            IconButton(
                onClick = { nav.navigate("forgot") },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "OTP Verification",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Enter the 6-digit code sent to your email",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(30.dp))

                // ✅ FIXED ALIGNMENT + VISIBILITY
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = (shake * 8 - 4).dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { i ->

                        OutlinedTextField(
                            value = digits[i],
                            onValueChange = { v ->

                                // ✅ paste support
                                if (v.length > 1) {
                                    v.take(6).forEachIndexed { idx, c ->
                                        digits[idx] = c.toString()
                                    }
                                    focus[5].requestFocus()
                                    return@OutlinedTextField
                                }

                                if (v.all { it.isDigit() }) {
                                    digits[i] = v
                                    if (v.isNotEmpty() && i < 5) {
                                        focus[i + 1].requestFocus()
                                    }
                                }

                                // ✅ backspace move
                                if (v.isEmpty() && i > 0) {
                                    focus[i - 1].requestFocus()
                                }
                            },
                            modifier = Modifier
                                .width(50.dp)     // ✅ wider so last digit visible
                                .height(58.dp)
                                .focusRequester(focus[i]),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                // ✅ Continue button (WORKING)
                Button(
                    onClick = {
                        verifyOtp(
                            nav,
                            email,
                            digits.joinToString(""),
                            onFail = {
                                msg = "Invalid OTP"
                                shakeTrigger++
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("verify OTP")
                }

                Spacer(Modifier.height(16.dp))

                // ✅ RESEND — calls same backend API
                if (seconds > 0) {
                    Text("Resend OTP in $seconds s", color = Color.Gray)
                } else {
                    TextButton(
                        onClick = {
                            seconds = 30

                            RetrofitClient.api.sendOtp(
                                ForgotRequest(email)
                            ).enqueue(object : retrofit2.Callback<ApiResponse> {
                                override fun onResponse(
                                    call: retrofit2.Call<ApiResponse>,
                                    response: retrofit2.Response<ApiResponse>
                                ) {
                                    msg = "OTP resent"
                                }

                                override fun onFailure(
                                    call: retrofit2.Call<ApiResponse>,
                                    t: Throwable
                                ) {
                                    msg = "Failed to resend"
                                }
                            })
                        }
                    ) {
                        Text("Resend OTP")
                    }
                }

                if (msg.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(msg, color = Color.Red)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focus[0].requestFocus()
    }
}

private fun verifyOtp(
    nav: NavController,
    email: String,
    otp: String,
    onFail: () -> Unit
) {
    if (otp.length != 6) return

    RetrofitClient.api.verifyOtp(
        OtpVerifyRequest(email, otp)
    ).enqueue(object : retrofit2.Callback<ApiResponse> {

        override fun onResponse(
            call: retrofit2.Call<ApiResponse>,
            response: retrofit2.Response<ApiResponse>
        ) {
            if (response.body()?.status == "success") {
                nav.navigate("reset/$email")
            } else onFail()
        }

        override fun onFailure(
            call: retrofit2.Call<ApiResponse>,
            t: Throwable
        ) {
            onFail()
        }
    })
}
