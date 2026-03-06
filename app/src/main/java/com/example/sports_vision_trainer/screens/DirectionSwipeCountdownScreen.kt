package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun DirectionSwipeCountdownScreen(
    nav: NavController,
    origin: String   // ✅ Added for routing
) {

    var label by remember { mutableStateOf("3") }

    LaunchedEffect(Unit) {

        delay(700)
        label = "2"

        delay(700)
        label = "1"

        delay(700)
        label = "START"

        delay(600)

        // ✅ UPDATED ROUTE WITH ORIGIN
        nav.navigate("direction_swipe_game/$origin") {
            popUpTo("direction_swipe_countdown/$origin") { inclusive = true }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 64.sp,
            color = Color.White
        )
    }
}
