package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ColorTapCountdownScreen(
    nav: NavController,
    origin: String   // ✅ Added for routing
) {

    var label by remember { mutableStateOf("3") }

    LaunchedEffect(Unit) {

        delay(800)
        label = "2"

        delay(800)
        label = "1"

        delay(800)
        label = "START"

        delay(700)

        // ✅ UPDATED ROUTE (origin added)
        nav.navigate("color_tap/$origin") {
            popUpTo("color_tap_countdown/$origin") { inclusive = true }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 64.sp, color = Color.White)
    }
}
