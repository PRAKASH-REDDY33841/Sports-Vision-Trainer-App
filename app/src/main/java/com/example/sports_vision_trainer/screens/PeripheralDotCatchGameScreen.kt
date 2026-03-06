package com.example.sports_vision_trainer.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun PeripheralDotCatchGameScreen(
    nav: NavController,
    origin: String   // ✅ Added origin
) {

    /* ---------- STATE ---------- */

    var score by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }

    var paused by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf("MEDIUM") }

    var showDot by remember { mutableStateOf(false) }
    var x by remember { mutableStateOf(0.dp) }
    var y by remember { mutableStateOf(0.dp) }

    var lastReaction by remember { mutableStateOf(0L) }
    var streak by remember { mutableStateOf(0) }

    val reactions = remember { mutableStateListOf<Long>() }
    var appearTime by remember { mutableStateOf(0L) }

    /* ---------- ANIMATION ---------- */

    val pulse = rememberInfiniteTransition(label = "pulse")

    val scale by pulse.animateFloat(
        0.8f, 1.25f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "scale"
    )

    val glowAlpha by pulse.animateFloat(
        0.35f, 0.9f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "alpha"
    )

    /* ---------- TIMER ---------- */

    LaunchedEffect(paused, timeLeft) {
        if (!paused && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }

        if (timeLeft == 0) {
            val avg = if (reactions.isNotEmpty())
                reactions.average().toLong() else 0L

            // ✅ UPDATED RESULT ROUTE
            nav.navigate(
                "reaction_result/$score/$avg/$misses/$score/peripheral/$origin"
            ) {
                popUpTo("peripheral_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- CONTINUOUS SPAWN LOOP ---------- */

    LaunchedEffect(paused, speed, timeLeft) {

        val baseDelay = when (speed) {
            "SLOW" -> 900L
            "MEDIUM" -> 650L
            else -> 450L
        }

        while (!paused && timeLeft > 0) {

            delay(baseDelay + Random.nextLong(200, 400))

            val zone = listOf("LEFT","RIGHT","TOP","BOTTOM").random()

            when(zone) {
                "LEFT" -> { x = 8.dp; y = Random.nextInt(140,720).dp }
                "RIGHT" -> { x = 360.dp; y = Random.nextInt(140,720).dp }
                "TOP" -> { x = Random.nextInt(40,360).dp; y = 90.dp }
                else -> { x = Random.nextInt(40,360).dp; y = 760.dp }
            }

            showDot = true
            appearTime = System.currentTimeMillis()

            delay(650)

            if (showDot) {
                misses++
                streak = 0
            }

            showDot = false
        }
    }

    /* ---------- UI ---------- */

    Box(
        Modifier.fillMaxSize().background(Color.Black)
    ) {

        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TIME $timeLeft", color = Color.White)
            Text(speed, color = Color.White)

            IconButton({
                paused = true
                showSettings = true
            }) {
                Icon(Icons.Default.Settings, null, tint = Color.White)
            }
        }

        if (showDot && !paused) {
            Box(
                Modifier
                    .offset(x, y)
                    .size((44 * scale).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color.Cyan.copy(glowAlpha), Color.Transparent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(26.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            val rt = System.currentTimeMillis() - appearTime
                            lastReaction = rt
                            reactions.add(rt)
                            score += (1 + streak / 3)
                            streak++
                            showDot = false
                        }
                )
            }
        }

        Text(
            "Misses: $misses",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    /* ---------- SETTINGS ---------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = {
                showSettings = false
                paused = false
            },
            confirmButton = {},
            containerColor = Color(0xFFEDE2D9),
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Drill Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {

                Column {

                    Text("Motion Speed")
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("SLOW","MEDIUM","FAST").forEach { s ->
                            FilterChip(
                                selected = speed == s,
                                onClick = { speed = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text("Duration")
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(20,30,60).forEach { t ->
                            FilterChip(
                                selected = timeLeft == t,
                                onClick = { timeLeft = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    Button(
                        onClick = { nav.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Exit Drill", color = Color.White)
                    }
                }
            }
        )
    }
}
