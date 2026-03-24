package com.example.sports_vision_trainer.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DoctorForgotPasswordScreen(nav: NavController) {

    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrownText = Color(0xFF2D1E16)
    val grayText = Color(0xFF757575)

    var step by remember { mutableIntStateOf(1) } // 1: Email, 2: OTP, 3: Reset
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                if (step > 1) step-- else nav.navigateUp() 
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = darkBrownText)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "VISION TRAINING",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrownText,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when(step) {
                1 -> "Forgot Password"
                2 -> "Verify Identity"
                else -> "Reset Password"
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkBrownText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when(step) {
                1 -> "Enter your professional clinical email to receive a secure access code."
                2 -> "A 6-digit code has been sent to $email. Please enter it below."
                else -> "Create a new secure password for your clinical account."
            },
            fontSize = 14.sp,
            color = grayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFFD32F2F), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.Transparent)
                .padding(horizontal = 8.dp)
        ) {
            Column {
                when (step) {
                    1 -> {
                        DoctorAuthLabel("CLINIC EMAIL")
                        DoctorAuthTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = "" },
                            placeholder = "dr.vane@clinic.com",
                            keyboardType = KeyboardType.Email
                        )
                    }
                    2 -> {
                        DoctorAuthLabel("SECURE CODE")
                        DoctorAuthTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 6) otp = it; errorMessage = "" },
                            placeholder = "000000",
                            keyboardType = KeyboardType.Number
                        )
                    }
                    3 -> {
                        DoctorAuthLabel("NEW PASSWORD")
                        DoctorAuthTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it; errorMessage = "" },
                            placeholder = "••••••••",
                            isPassword = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = grayText)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        DoctorAuthLabel("CONFIRM PASSWORD")
                        DoctorAuthTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorMessage = "" },
                            placeholder = "••••••••",
                            isPassword = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        when (step) {
                            1 -> {
                                if (email.isEmpty()) {
                                    errorMessage = "Please enter your email"
                                    return@Button
                                }
                                RetrofitClient.api.doctorSendOtp(ForgotRequest(email))
                                    .enqueue(object : Callback<ApiResponse> {
                                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                            if (response.isSuccessful && response.body()?.status == "success") {
                                                step = 2
                                            } else {
                                                errorMessage = response.body()?.msg ?: "Email not found"
                                            }
                                        }
                                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                            errorMessage = "Connection error"
                                        }
                                    })
                            }
                            2 -> {
                                if (otp.length != 6) {
                                    errorMessage = "Enter 6-digit code"
                                    return@Button
                                }
                                RetrofitClient.api.doctorVerifyOtp(OtpVerifyRequest(email, otp))
                                    .enqueue(object : Callback<ApiResponse> {
                                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                            if (response.isSuccessful && response.body()?.status == "success") {
                                                step = 3
                                            } else {
                                                errorMessage = response.body()?.msg ?: "Invalid code"
                                            }
                                        }
                                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                            errorMessage = "Connection error"
                                        }
                                    })
                            }
                            3 -> {
                                if (newPassword.length < 6) {
                                    errorMessage = "Password too short"
                                    return@Button
                                }
                                if (newPassword != confirmPassword) {
                                    errorMessage = "Passwords do not match"
                                    return@Button
                                }
                                RetrofitClient.api.doctorResetPasswordFinal(ResetRequest(email, newPassword))
                                    .enqueue(object : Callback<ApiResponse> {
                                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                            if (response.isSuccessful && response.body()?.status == "success") {
                                                Toast.makeText(ctx, "Password reset successfully!", Toast.LENGTH_LONG).show()
                                                nav.navigate("doctor_login") {
                                                    popUpTo("doctor_forgot") { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = "Reset failed"
                                            }
                                        }
                                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                            errorMessage = "Connection error"
                                        }
                                    })
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBrownText)
                ) {
                    Text(
                        text = if (step < 3) "CONTINUE" else "RESET PASSWORD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color(0xFFBDBDBD)) },
        trailingIcon = trailingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
    )
}

@Composable
fun DoctorAuthLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D1E16),
        letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
}
