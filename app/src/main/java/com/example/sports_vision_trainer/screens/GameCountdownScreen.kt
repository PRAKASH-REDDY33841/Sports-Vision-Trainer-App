package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun GameCountdownScreen(
    nav: NavController,
    game: String,
    origin: String   // ✅ Added origin support
) {
    var count by remember { mutableStateOf(3) }
    var showStart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        while (count > 0) {
            delay(1000)
            count--
        }

        showStart = true
        delay(700)

        // ✅ UPDATED ROUTES WITH ORIGIN
        val nextRoute = when (game) {
            "moving" -> "moving_target_game/$origin"
            "gonogo" -> "gonogo_game/$origin"
            "peripheral" -> "peripheral_game/$origin"
            "colordirection" -> "color_direction_game/$origin"
            "burst" -> "countdown_burst_game/$origin"
            "random" -> "random_target_game/$origin"
            "dualcolor" -> "dual_color_game/$origin"
            "number" -> "number_reaction_game/$origin"
            "arrowrush" -> "arrow_rush_game/$origin"
            "distraction" -> "distraction_layer/$origin"
            "memory" -> "memory_grid/$origin"
            "soundvisual" -> "sound_visual_game/$origin"




            else -> "sports"
        }


        nav.navigate(nextRoute) {
            popUpTo("countdown/$game/$origin") { inclusive = true }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (showStart) "START" else count.toString(),
            color = Color.White,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
