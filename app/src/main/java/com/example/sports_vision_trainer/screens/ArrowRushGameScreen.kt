package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ArrowRushGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------------- SETTINGS ---------------- */

    var showSettings by remember { mutableStateOf(false) }
    var sessionSeconds by remember { mutableStateOf(30) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }

    /* ---------------- STATE ---------------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var showArrow by remember { mutableStateOf(false) }
    var currentArrow by remember { mutableStateOf("⬅") }

    var score by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var reactionTotal by remember { mutableStateOf(0L) }
    var lastSpawn by remember { mutableStateOf(0L) }

    val paused = showSettings

    /* ---------------- RESET TIMER ---------------- */

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------------- TIMER ---------------- */

    LaunchedEffect(showSettings, sessionSeconds) {
        while (isActive && timeLeft > 0) {
            if (!paused) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {

            val avg = if (score == 0) 0 else reactionTotal / score
            val finalScore =
                score * 120 - misses * 40 - (avg / 5).toInt()

            nav.navigate(
                "reaction_result/$score/$avg/$misses/$finalScore/arrowrush/$origin"
            ) {
                popUpTo("arrow_rush_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------------- SPEED CONTROL ---------------- */

    fun spawnDelay(): Long {
        val base = when (motionLabel) {
            "SLOW" -> 1400L
            "MEDIUM" -> 1000L
            else -> 700L
        }
        val ramp = (score * 8).coerceAtMost(400)
        return (base - ramp).coerceAtLeast(300)
    }

    /* ---------------- ARROW ENGINE ---------------- */

    LaunchedEffect(showSettings, timeLeft, motionLabel) {

        while (isActive && timeLeft > 0) {

            if (paused) {
                delay(200)
                continue
            }

            delay(spawnDelay())

            currentArrow = listOf("⬅","➡","⬆","⬇").random()
            lastSpawn = System.currentTimeMillis()
            showArrow = true

            delay(800)

            if (showArrow) {
                misses++
                showArrow = false
            }
        }
    }

    /* ---------------- UI ---------------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(showArrow) {

                detectTapGestures(
                    onTap = {

                        if (!showArrow || paused) return@detectTapGestures

                        val rt =
                            System.currentTimeMillis() - lastSpawn

                        reactionTotal += rt
                        score++
                        showArrow = false
                    }
                )
            }
    ) {

        /* ---------- TOP BAR ---------- */

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("TIME $timeLeft", color = Color.White)

            Spacer(Modifier.weight(1f))

            Text(motionLabel, color = Color.White)

            Spacer(Modifier.weight(1f))

            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    showSettings = true
                }
            )
        }

        /* ---------- ARROW ---------- */

        if (showArrow) {

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.Center)
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        currentArrow,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        /* ---------- FOOTER ---------- */

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Score $score", color = Color.White)
            Text("Misses $misses", color = Color.Gray)
        }
    }

    /* ---------------- SETTINGS PANEL ---------------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SLOW","MEDIUM","FAST").forEach { s ->
                            FilterChip(
                                selected = motionLabel == s,
                                onClick = { motionLabel = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Text("Duration")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(20,30,60).forEach { t ->
                            FilterChip(
                                selected = sessionSeconds == t,
                                onClick = { sessionSeconds = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            showSettings = false
                            nav.popBackStack()
                        },
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
