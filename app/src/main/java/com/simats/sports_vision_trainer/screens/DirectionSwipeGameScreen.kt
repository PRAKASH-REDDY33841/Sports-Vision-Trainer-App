package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun DirectionSwipeGameScreen(
    nav: NavController,
    origin: String   // ✅ Added for routing
) {

    // ---------- SETTINGS ----------
    var showSettings by remember { mutableStateOf(false) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var sessionSeconds by remember { mutableStateOf(30) }
    var highlightDuration by remember { mutableStateOf(1700L) }

    // ---------- STATE ----------
    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var activeIndex by remember { mutableStateOf(-1) }

    var taps by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var reactionTotal by remember { mutableStateOf(0L) }
    var lastSpawnTime by remember { mutableStateOf(0L) }

    val rows = 8
    val cols = 10
    val gridCount = rows * cols

    val paused = showSettings

    // ---------- RESET TIMER ----------
    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    // ---------- TIMER ----------
    LaunchedEffect(showSettings, sessionSeconds) {
        while (isActive && timeLeft > 0) {
            if (!paused) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {
            val avg = if (taps == 0) 0 else reactionTotal / taps
            val score = taps * 110 - misses * 40 - (avg / 5).toInt()

            // ✅ UPDATED RESULT ROUTE WITH ORIGIN
            nav.navigate("reaction_result/$taps/$avg/$misses/$score/swipe/$origin") {
                popUpTo("direction_swipe_game/$origin") { inclusive = true }
            }
        }
    }

    // ---------- RANDOM TARGET ENGINE ----------
    LaunchedEffect(showSettings, highlightDuration, timeLeft) {
        while (isActive && timeLeft > 0) {

            if (paused) {
                delay(200)
                continue
            }

            activeIndex = Random.nextInt(gridCount)
            lastSpawnTime = System.currentTimeMillis()

            delay(highlightDuration)

            if (activeIndex != -1) {
                misses++
                activeIndex = -1
            }
        }
    }

    // ---------- UI ----------
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                modifier = Modifier.clickable { showSettings = true }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                repeat(rows) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        repeat(cols) { col ->

                            val index = row * cols + col
                            val isActive = index == activeIndex

                            Box(
                                Modifier
                                    .size(32.dp)
                                    .background(
                                        if (isActive) Color.Red else Color.White,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {

                                        if (paused) return@clickable

                                        if (isActive) {
                                            taps++
                                            val rt =
                                                System.currentTimeMillis() - lastSpawnTime
                                            reactionTotal += rt
                                            activeIndex = -1
                                        } else {
                                            misses++
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpeedChip("SLOW", motionLabel) {
                            motionLabel = "SLOW"
                            highlightDuration = 2200
                        }
                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"
                            highlightDuration = 1700
                        }
                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"
                            highlightDuration = 1100
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
                            nav.popBackStack()
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
