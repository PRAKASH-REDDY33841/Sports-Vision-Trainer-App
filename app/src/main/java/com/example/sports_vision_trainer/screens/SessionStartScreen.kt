package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SessionStartScreen(
    nav: NavController,
    origin: String   // ✅ ADDED (route support only)
) {

    var count by remember { mutableStateOf(3) }
    var showStart by remember { mutableStateOf(false) }

    // ✅ Countdown + auto start
    LaunchedEffect(Unit) {

        // 3 → 2 → 1
        while (count > 0) {
            delay(1000)
            count--
        }

        // show START
        showStart = true
        delay(800)

        // ✅ AUTO START GAME PAGE (UPDATED ROUTE ONLY)
        nav.navigate("reaction_game/$origin") {
            popUpTo("session_start/$origin") { inclusive = true }
        }
    }

    Scaffold { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (showStart) "START" else count.toString(),
                fontSize = 72.sp
            )
        }
    }
}
