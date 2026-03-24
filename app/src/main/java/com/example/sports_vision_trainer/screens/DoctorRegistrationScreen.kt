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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.ApiResponse
import com.example.sports_vision_trainer.network.DoctorRegisterRequest
import com.example.sports_vision_trainer.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorRegistrationScreen(nav: NavController) {

    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrownText = Color(0xFF2D1E16)
    val grayText = Color(0xFF757575)

    var fullName by remember { mutableStateOf("") }
    var medicalLicense by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var clinicEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var medicalLicenseError by remember { mutableStateOf<String?>(null) }
    var hospitalNameError by remember { mutableStateOf<String?>(null) }
    var clinicEmailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }

    var fullNameHasFocus by remember { mutableStateOf(false) }
    var medicalLicenseHasFocus by remember { mutableStateOf(false) }
    var hospitalNameHasFocus by remember { mutableStateOf(false) }
    var clinicEmailHasFocus by remember { mutableStateOf(false) }
    var passwordHasFocus by remember { mutableStateOf(false) }
    var confirmPasswordHasFocus by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val ctx = LocalContext.current

    val validatePasswordRules = { pass: String ->
        when {
            pass.isEmpty() -> "Password is required"
            !pass.contains(Regex("[A-Z]")) -> "Password must contain at least one uppercase letter"
            !pass.contains(Regex("[0-9]")) -> "Password must contain at least one number"
            !pass.contains(Regex("[^A-Za-z0-9]")) -> "Password must contain at least one special character"
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "VISION TRAINING",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrownText,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Doctor Registration",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkBrownText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Apply for professional access to the\nOcular Athletic practitioner portal and\nathlete management tools.",
            fontSize = 14.sp,
            color = grayText,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (serverError != null) {
            Text(
                text = serverError!!,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {

                // Full Name
                LabelText("FULL NAME")
                RegistrationTextField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        if (it.isNotEmpty()) fullNameError = null 
                        serverError = null
                    },
                    placeholder = "Dr. Julian Vane",
                    errorMessage = fullNameError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            fullNameHasFocus = true
                        } else if (fullNameHasFocus && fullName.isEmpty()) {
                            fullNameError = "Full Name is required"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Medical License ID
                LabelText("MEDICAL LICENSE ID")
                RegistrationTextField(
                    value = medicalLicense,
                    onValueChange = { 
                        medicalLicense = it
                        if (it.isNotEmpty()) medicalLicenseError = null 
                        serverError = null
                    },
                    placeholder = "MED-992031",
                    errorMessage = medicalLicenseError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            medicalLicenseHasFocus = true
                        } else if (medicalLicenseHasFocus && medicalLicense.isEmpty()) {
                            medicalLicenseError = "Medical License is required"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Hospital/Clinic Name
                LabelText("HOSPITAL/CLINIC Name")
                RegistrationTextField(
                    value = hospitalName,
                    onValueChange = { 
                        hospitalName = it
                        if (it.isNotEmpty()) hospitalNameError = null 
                        serverError = null
                    },
                    placeholder = "Metropolitan Performance Vision",
                    errorMessage = hospitalNameError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            hospitalNameHasFocus = true
                        } else if (hospitalNameHasFocus && hospitalName.isEmpty()) {
                            hospitalNameError = "Hospital/Clinic Name is required"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Clinic Email
                LabelText("CLINIC EMAIL")
                RegistrationTextField(
                    value = clinicEmail,
                    onValueChange = { 
                        clinicEmail = it
                        if (it.isNotEmpty()) clinicEmailError = null 
                        serverError = null
                    },
                    placeholder = "julian.vane@clinic.com",
                    keyboardType = KeyboardType.Email,
                    errorMessage = clinicEmailError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            clinicEmailHasFocus = true
                        } else if (clinicEmailHasFocus && clinicEmail.isEmpty()) {
                            clinicEmailError = "Clinic Email is required"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Create Password
                LabelText("CREATE PASSWORD")
                RegistrationTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = validatePasswordRules(it)
                        if (confirmPassword.isNotEmpty() && confirmPassword != it) {
                            confirmPasswordError = "Passwords do not match"
                        } else if (confirmPassword == it) {
                            confirmPasswordError = null
                        }
                        serverError = null
                    },
                    placeholder = "••••••••",
                    isPassword = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = grayText)
                        }
                    },
                    errorMessage = passwordError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            passwordHasFocus = true
                        } else if (passwordHasFocus) {
                            passwordError = validatePasswordRules(password)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password
                LabelText("CONFIRM PASSWORD")
                RegistrationTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        if (it != password) {
                            confirmPasswordError = "Passwords do not match"
                        } else {
                            confirmPasswordError = null
                        }
                        serverError = null
                    },
                    placeholder = "••••••••",
                    isPassword = true,
                    trailingIcon = if (password == confirmPassword && password.isNotEmpty() && passwordError == null) {
                        {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Match", tint = Color(0xFF4CAF50))
                        }
                    } else null,
                    errorMessage = confirmPasswordError,
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            confirmPasswordHasFocus = true
                        } else if (confirmPasswordHasFocus) {
                            if (confirmPassword.isEmpty()) confirmPasswordError = "Please confirm your password"
                            else if (confirmPassword != password) confirmPasswordError = "Passwords do not match"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Create Account Button
                Button(
                    onClick = {
                        var hasError = false
                        if (fullName.isEmpty()) { fullNameError = "Full Name is required"; hasError = true }
                        if (medicalLicense.isEmpty()) { medicalLicenseError = "Medical License is required"; hasError = true }
                        if (hospitalName.isEmpty()) { hospitalNameError = "Hospital/Clinic Name is required"; hasError = true }
                        if (clinicEmail.isEmpty()) { clinicEmailError = "Clinic Email is required"; hasError = true }
                        
                        val passError = validatePasswordRules(password)
                        if (passError != null) {
                            passwordError = passError
                            hasError = true
                        }
                        if (confirmPassword != password) {
                            confirmPasswordError = "Passwords do not match"
                            hasError = true
                        }

                        if (hasError) return@Button
                        
                        val req = DoctorRegisterRequest(
                            full_name = fullName,
                            medical_license = medicalLicense,
                            hospital_name = hospitalName,
                            clinic_email = clinicEmail,
                            password = password
                        )
                        
                        RetrofitClient.api.doctorRegister(req).enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(
                                call: Call<ApiResponse>,
                                response: Response<ApiResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    if (body?.status == "success") {
                                        Toast.makeText(ctx, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                        nav.navigate("doctor_login") {
                                            popUpTo("doctor_register") { inclusive = true }
                                        }
                                    } else {
                                        serverError = body?.message ?: "Registration failed"
                                    }
                                } else {
                                    serverError = "Server error occurred"
                                }
                            }

                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                serverError = "Network error: ${t.message}"
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBrownText)
                ) {
                    Text(
                        text = "CREATE DOCTOR ACCOUNT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 12.sp,
                        color = grayText
                    )
                    Text(
                        text = "Login here",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrownText,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { nav.navigate("doctor_login") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            color = Color(0xFFEADFCD),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CERTIFIED PERFORMANCE STANDARDS",
            fontSize = 9.sp,
            color = Color(0xFFAFA79F),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "SUPPORT", fontSize = 9.sp, color = grayText, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = "LEGAL", fontSize = 9.sp, color = grayText, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = "PRIVACY", fontSize = 9.sp, color = grayText, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "© 2024 OCULAR ATHLETIC",
            fontSize = 9.sp,
            color = grayText,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D1E16),
        letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    onFocusChange: (Boolean) -> Unit = {}
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFFBDBDBD)) },
            trailingIcon = trailingIcon,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
