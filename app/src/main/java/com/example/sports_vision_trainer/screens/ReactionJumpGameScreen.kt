package com.example.sports_vision_trainer.screens

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
import kotlin.random.Random

@Composable
fun ReactionJumpGameScreen(
    nav: NavController,
    origin: String
) {

    var showSettings by remember { mutableStateOf(false) }

    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var jumpIntervalMs by remember { mutableStateOf(900L) }
    var sessionSeconds by remember { mutableStateOf(60) }

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var taps by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }

    var targetX by remember { mutableStateOf(0.4f) }
    var targetY by remember { mutableStateOf(0.5f) }

    var lastSpawnTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var reactionTotal by remember { mutableStateOf(0L) }

    val paused = showSettings

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------- TIMER ---------- */

    LaunchedEffect(paused, sessionSeconds) {
        while (timeLeft > 0) {
            if (!paused) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {

            val avg = if (taps == 0) 0 else reactionTotal / taps

            val totalAttempts = taps + misses
            val accuracy =
                if (totalAttempts == 0) 0f
                else taps.toFloat() / totalAttempts

            val speedFactor =
                if (avg == 0L) 0f
                else 1000f / avg

            val score =
                (taps * 10 +
                        accuracy * 200 +
                        speedFactor * 100).toInt()

            nav.navigate(
                "reaction_result/$taps/$avg/$misses/$score/jump/$origin"
            ) {
                popUpTo("reaction_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- TARGET ENGINE ---------- */

    LaunchedEffect(paused, jumpIntervalMs) {
        while (timeLeft > 0) {
            if (!paused) {
                delay(jumpIntervalMs)
                targetX = Random.nextFloat()
                targetY = Random.nextFloat()
                lastSpawnTime = System.currentTimeMillis()
            } else delay(200)
        }
    }

    /* ---------- UI ---------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable {
                if (!paused && timeLeft > 0) misses++
            }
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$timeLeft", color = Color.White)
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

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val maxW = maxWidth
            val maxH = maxHeight

            Box(
                Modifier
                    .offset(
                        x = maxW * targetX - 30.dp,
                        y = maxH * targetY - 30.dp
                    )
                    .size(60.dp)
                    .background(Color.White, CircleShape)
                    .clickable {
                        if (!paused && timeLeft > 0) {
                            taps++
                            val rt =
                                System.currentTimeMillis() - lastSpawnTime
                            reactionTotal += rt
                        }
                    }
            )
        }
    }

    /* ---------- SETTINGS ---------- */

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SpeedChip("SLOW", motionLabel) {
                            motionLabel = "SLOW"; jumpIntervalMs = 1200L
                        }
                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"; jumpIntervalMs = 900L
                        }
                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"; jumpIntervalMs = 600L
                        }
                    }

                    Text("Session Time")

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TimeChip("10s", 10, sessionSeconds) { sessionSeconds = it }
                            TimeChip("30s", 30, sessionSeconds) { sessionSeconds = it }
                            TimeChip("1m", 60, sessionSeconds) { sessionSeconds = it }
                        }
                        Row {
                            TimeChip("5m", 300, sessionSeconds) { sessionSeconds = it }
                        }
                    }

                    Button(
                        onClick = {
                            showSettings = false

                            if (origin == "home") {
                                nav.navigate("home/{email}/{name}") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            } else {
                                nav.navigate("sports") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Exit Drill")
                    }
                }
            }
        )
    }
}

@Composable
private fun SpeedChip(label: String, current: String, onClick: () -> Unit) {
    FilterChip(
        selected = current == label,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun TimeChip(label: String, sec: Int, current: Int, onPick: (Int) -> Unit) {
    FilterChip(
        selected = current == sec,
        onClick = { onPick(sec) },
        label = { Text(label) }
    )
}
