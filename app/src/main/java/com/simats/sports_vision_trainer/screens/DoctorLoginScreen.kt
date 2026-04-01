package com.simats.sports_vision_trainer.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simats.sports_vision_trainer.network.ApiResponse
import com.simats.sports_vision_trainer.network.DoctorLoginRequest
import com.simats.sports_vision_trainer.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorLoginScreen(nav: NavController) {

    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrownText = Color(0xFF2D1E16)
    val fieldBackground = Color.White
    val lightBlueBadge = Color(0xFFC7E5EB)
    val darkBlueText = Color(0xFF1E5868)
    val grayText = Color(0xFF757575)
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }

    var emailHasFocus by remember { mutableStateOf(false) }
    var passwordHasFocus by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Text(
            text = "OCULAR ATHLETIC",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrownText,
            letterSpacing = 3.sp
        )
        Text(
            text = "VISION TRAINING",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkBrownText,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            color = Color(0xFFEADFCD),
            thickness = 1.dp
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
                .background(
                    color = Color.Transparent
                )
        ) {
            Column {
                
                Box(
                    modifier = Modifier
                        .background(lightBlueBadge, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "CLINICAL ACCESS",
                        color = darkBlueText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Doctor Login",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrownText
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Manage patient performance and training\nprotocols.",
                    fontSize = 14.sp,
                    color = grayText,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email Field
                Text(
                    text = "MEDICAL EMAIL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrownText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { 
                        email = it 
                        if (it.isNotEmpty()) emailError = null
                        serverError = null
                    },
                    placeholder = { Text("name@clinic.com", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Email Icon",
                            tint = grayText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                emailHasFocus = true
                            } else if (emailHasFocus && email.isEmpty()) {
                                emailError = "Email is required"
                            }
                        },
                    isError = emailError != null,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = fieldBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Password Field
                Text(
                    text = "CREDENTIALS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrownText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { 
                        password = it 
                        if (it.isNotEmpty()) passwordError = null
                        serverError = null
                    },
                    placeholder = { Text("••••••••", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Icon",
                            tint = grayText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = grayText)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                passwordHasFocus = true
                            } else if (passwordHasFocus && password.isEmpty()) {
                                passwordError = "Password is required"
                            }
                        },
                    isError = passwordError != null,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = fieldBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Forgot password?",
                    fontSize = 12.sp,
                    color = darkBlueText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { nav.navigate("doctor_forgot") }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Login Button
                Button(
                    onClick = {
                        var hasError = false
                        if (email.isEmpty()) { emailError = "Email is required"; hasError = true }
                        if (password.isEmpty()) { passwordError = "Password is required"; hasError = true }
                        if (hasError) return@Button

                        RetrofitClient.api.doctorLogin(DoctorLoginRequest(email, password))
                            .enqueue(object : Callback<ApiResponse> {
                                override fun onResponse(
                                    call: Call<ApiResponse>,
                                    response: Response<ApiResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        if (body?.status == "success") {
                                            Toast.makeText(ctx, "Login Successful!", Toast.LENGTH_LONG).show()
                                            val name = body.username ?: "Doctor"
                                            val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                                            val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                                            nav.navigate("doctor_home/$encodedEmail/$encodedName") {
                                                popUpTo("doctor_login") { inclusive = true }
                                            }
                                        } else {
                                            serverError = body?.message ?: "Login failed"
                                        }
                                    } else {
                                        serverError = "Server error"
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOGIN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward Icon",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Register & Line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(2.dp)
                            .background(darkBrownText)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 12.sp,
                        color = grayText
                    )
                    Text(
                        text = "Register here ->",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrownText,
                        modifier = Modifier.clickable { nav.navigate("doctor_register") }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Footer texts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Not a medical professional? ",
                fontSize = 12.sp,
                color = grayText
            )
            Text(
                text = "Athlete Login",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrownText,
                modifier = Modifier.clickable { nav.navigate("login") }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Secure",
                tint = Color(0xFFAFA79F),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "SECURE HIPAA NODE",
                fontSize = 10.sp,
                color = Color(0xFFAFA79F),
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "PROFESSIONAL PERFORMANCE ECOSYSTEM",
            fontSize = 9.sp,
            color = Color(0xFFD6CFC9),
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
