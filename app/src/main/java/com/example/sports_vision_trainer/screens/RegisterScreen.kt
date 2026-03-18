package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(nav: NavController) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // touched + blur flags
    var uTouched by remember { mutableStateOf(false) }
    var uBlur by remember { mutableStateOf(false) }

    var eTouched by remember { mutableStateOf(false) }
    var eBlur by remember { mutableStateOf(false) }

    var pTouched by remember { mutableStateOf(false) }
    var pBlur by remember { mutableStateOf(false) }

    var cTouched by remember { mutableStateOf(false) }
    var cBlur by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf("") }   // ✅ ADDED

    val confirmMatch = confirm.isNotEmpty() && confirm == password

    // ✅ PASSWORD VALIDATION RULE
    val passwordValid = password.matches(
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,16}$")
    )

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "VISION TRAINING FOR ATHLETES",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        // USERNAME
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            isError = uTouched && uBlur && username.isBlank(),
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) uTouched = true
                    else if (uTouched) uBlur = true
                }
        )
        if (uTouched && uBlur && username.isBlank())
            Text("* Fill this field", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(12.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = eTouched && eBlur && email.isBlank(),
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) eTouched = true
                    else if (eTouched) eBlur = true
                }
        )
        if (eTouched && eBlur && email.isBlank())
            Text("* Fill this field", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = pTouched && pBlur && !passwordValid,
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) pTouched = true
                    else if (pTouched) pBlur = true
                }
        )
        if (pTouched && pBlur && !passwordValid)
            Text(
                "* Password must contain uppercase, lowercase, number & special character (8-16)",
                color = MaterialTheme.colorScheme.error
            )

        Spacer(Modifier.height(12.dp))

        // CONFIRM PASSWORD
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = cTouched && cBlur && !confirmMatch,
            trailingIcon = {
                if (confirmMatch)
                    Icon(Icons.Default.Check, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) cTouched = true
                    else if (cTouched) cBlur = true
                }
        )
        if (cTouched && cBlur && !confirmMatch)
            Text("* Passwords must match", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (username.isBlank() || email.isBlank() || !passwordValid || !confirmMatch)
                    return@Button

                RetrofitClient.api.register(
                    RegisterRequest(username, email, password)
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, r: Response<ApiResponse>) {
                        val response = r.body()   // ✅ ADDED

                        if (response?.status == "success") {
                            showDialog = true
                        } else {
                            errorMsg = response?.message ?: "Error"   // ✅ ADDED
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Register") }

        // ✅ SHOW ERROR MESSAGE
        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = { nav.navigate("login") }) {
            Text("Already registered? Login")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button({
                    showDialog = false
                    nav.navigate("login")
                }) { Text("Go Login") }
            },
            title = { Text("Registered Successfully") },
            text = { Text("Account created") }
        )
    }
}