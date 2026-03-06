package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
fun MovingCountdownScreen(nav: NavController) {

    var count by remember { mutableStateOf(3) }
    var showStart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        // 3 → 2 → 1
        while (count > 0) {
            delay(1000)
            count--
        }

        // show START
        showStart = true
        delay(800)

        // navigate to moving target game
        nav.navigate("moving_target_game") {
            popUpTo("moving_countdown") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),   // ✅ black background
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = if (showStart) "START" else count.toString(),
            color = Color.White,        // ✅ white text
            fontSize = 64.sp,           // ✅ big text
            fontWeight = FontWeight.Bold
        )
    }
}
