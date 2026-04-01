package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun CountdownBurstGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------- SETTINGS ---------- */

    var showSettings by remember { mutableStateOf(false) }
    var speedLabel by remember { mutableStateOf("MEDIUM") }
    var sessionSeconds by remember { mutableStateOf(30) }
    var minDelay by remember { mutableStateOf(800L) }
    var maxDelay by remember { mutableStateOf(1500L) }

    /* ---------- STATE ---------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var showTapSignal by remember { mutableStateOf(false) }

    var taps by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var reactionTotal by remember { mutableStateOf(0L) }

    var appearTime by remember { mutableStateOf(0L) }

    val paused = showSettings

    /* ---------- RESET TIMER ---------- */

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------- GAME TIMER ---------- */

    LaunchedEffect(paused, sessionSeconds) {

        while (isActive && timeLeft > 0) {
            if (!paused) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {

            val avg = if (taps == 0) 0 else reactionTotal / taps
            val score = taps * 120 - misses * 40 - (avg / 5).toInt()

            nav.navigate(
                "reaction_result/$taps/$avg/$misses/$score/burst/sports"
            ) {
                popUpTo("countdown_burst_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- STIMULUS ENGINE ---------- */

    LaunchedEffect(paused, minDelay, maxDelay, timeLeft) {

        while (isActive && timeLeft > 0) {

            if (paused) {
                delay(200)
                continue
            }

            delay(Random.nextLong(minDelay, maxDelay))

            if (paused || timeLeft <= 0) continue

            showTapSignal = true
            appearTime = System.currentTimeMillis()

            delay(800)

            if (showTapSignal) {
                misses++
                showTapSignal = false
            }
        }
    }

    /* ---------- UI ---------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(if (showTapSignal) Color.Green else Color.Black)
            .clickable {

                if (!paused && showTapSignal) {
                    val rt =
                        System.currentTimeMillis() - appearTime
                    reactionTotal += rt
                    taps++
                    showTapSignal = false
                }
            }
    ) {

        /* ---------- TOP BAR ---------- */

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("TIME $timeLeft", color = Color.White)

            Spacer(Modifier.weight(1f))

            Text(speedLabel, color = Color.White)

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

        /* ---------- CENTER TEXT ---------- */

        if (showTapSignal) {
            Text(
                "TAP!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }

    /* ---------- SETTINGS DIALOG ---------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        SpeedChip("SLOW", speedLabel) {
                            speedLabel = "SLOW"
                            minDelay = 1200
                            maxDelay = 2000
                        }

                        SpeedChip("MEDIUM", speedLabel) {
                            speedLabel = "MEDIUM"
                            minDelay = 800
                            maxDelay = 1500
                        }

                        SpeedChip("FAST", speedLabel) {
                            speedLabel = "FAST"
                            minDelay = 400
                            maxDelay = 900
                        }
                    }

                    Text("Duration")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DurationChip("20s", 20, sessionSeconds) { sessionSeconds = it }
                        DurationChip("30s", 30, sessionSeconds) { sessionSeconds = it }
                        DurationChip("60s", 60, sessionSeconds) { sessionSeconds = it }
                    }

                    Button(
                        onClick = {
                            showSettings = false
                            nav.navigate("sports") {
                                popUpTo(0)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exit Drill")
                    }
                }
            }
        )
    }
}

/* ---------- REUSABLE CHIPS ---------- */

@Composable
private fun SpeedChip(label: String, current: String, onPick: () -> Unit) {
    FilterChip(
        selected = current == label,
        onClick = onPick,
        label = { Text(label) }
    )
}

@Composable
private fun DurationChip(label: String, value: Int, current: Int, onPick: (Int) -> Unit) {
    FilterChip(
        selected = current == value,
        onClick = { onPick(value) },
        label = { Text(label) }
    )
}
