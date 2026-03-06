package com.example.sports_vision_trainer.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun MovingTargetTapGameScreen(
    nav: NavController,
    origin: String   // ✅ Added for routing
) {

    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var paused by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf("MEDIUM") }

    val reactions = remember { mutableStateListOf<Long>() }
    var appearTime by remember { mutableStateOf(System.currentTimeMillis()) }

    /* ---------------- TIMER ---------------- */

    LaunchedEffect(timeLeft, paused) {
        if (!paused && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }

        if (timeLeft == 0) {
            val avg = if (reactions.isNotEmpty())
                reactions.average().toLong() else 0L

            // ✅ UPDATED RESULT ROUTE WITH ORIGIN
            nav.navigate(
                "reaction_result/$score/$avg/0/$score/moving/$origin"
            ) {
                popUpTo("moving_target_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------------- SMOOTH CENTER ZIGZAG ---------------- */

    val duration = when (speed) {
        "SLOW" -> 2600
        "MEDIUM" -> 1700
        else -> 1000
    }

    val infinite = rememberInfiniteTransition()

    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing)
        )
    )

    val x = (40 + progress * 280).dp
    val y = (420 + 140 * sin(progress * 6.28)).dp

    /* ---------------- UI ---------------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TIME $timeLeft", color = Color.White)
            Text(speed, color = Color.White)

            IconButton(onClick = {
                paused = true
                showSettings = true
            }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        if (!paused) {
            Box(
                Modifier
                    .offset(x, y)
                    .size(64.dp)
                    .background(Color.Red, CircleShape)
                    .clickable {
                        val rt = System.currentTimeMillis() - appearTime
                        reactions.add(rt)
                        score++
                        appearTime = System.currentTimeMillis()
                    }
            )
        }
    }

    /* ---------------- SETTINGS ---------------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = {
                showSettings = false
                paused = false
                appearTime = System.currentTimeMillis()
            },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column {

                    Text("Motion Speed")
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SLOW", "MEDIUM", "FAST").forEach { s ->
                            FilterChip(
                                selected = speed == s,
                                onClick = { speed = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Duration")
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(20, 30, 60).forEach { t ->
                            FilterChip(
                                selected = timeLeft == t,
                                onClick = { timeLeft = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { nav.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exit Drill")
                    }
                }
            }
        )
    }
}
