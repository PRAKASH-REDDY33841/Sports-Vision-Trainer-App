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
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun RandomTargetGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------- SETTINGS ---------- */

    var showSettings by remember { mutableStateOf(false) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var spawnDelayMs by remember { mutableStateOf(900L) }
    var sessionSeconds by remember { mutableStateOf(30) }

    /* ---------- STATE ---------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var taps by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }

    var targetX by remember { mutableStateOf(0.5f) }
    var targetY by remember { mutableStateOf(0.5f) }

    var lastSpawnTime by remember { mutableStateOf(0L) }
    var reactionTotal by remember { mutableStateOf(0L) }

    val paused = showSettings

    /* ---------- RESET TIMER WHEN DURATION CHANGES ---------- */

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------- TIMER ---------- */

    LaunchedEffect(paused, sessionSeconds) {

        while (isActive && timeLeft > 0) {

            if (!paused) {
                delay(1000)
                timeLeft--
            } else {
                delay(200)
            }
        }

        if (timeLeft <= 0) {

            val avg = if (taps == 0) 0 else reactionTotal / taps
            val score = taps * 100 - misses * 30 - (avg / 5).toInt()

            nav.navigate(
                "reaction_result/$taps/$avg/$misses/$score/random/$origin"
            ) {
                popUpTo("random_target_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- TARGET ENGINE ---------- */

    LaunchedEffect(paused, spawnDelayMs, timeLeft) {

        while (isActive && timeLeft > 0) {

            if (paused) {
                delay(200)
                continue
            }

            delay(spawnDelayMs)

            targetX = Random.nextFloat()
            targetY = Random.nextFloat()
            lastSpawnTime = System.currentTimeMillis()
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

        // ===== TOP BAR =====

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
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

        // ===== TARGET =====

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
                    .background(Color.Cyan, CircleShape)
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

    /* ---------- SETTINGS DIALOG ---------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = {
                showSettings = false
            },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        SpeedChip("SLOW", motionLabel) {
                            motionLabel = "SLOW"
                            spawnDelayMs = 1200
                        }

                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"
                            spawnDelayMs = 900
                        }

                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"
                            spawnDelayMs = 600
                        }
                    }

                    Text("Duration")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        DurationChip("20s", 20, sessionSeconds) {
                            sessionSeconds = it
                        }

                        DurationChip("30s", 30, sessionSeconds) {
                            sessionSeconds = it
                        }

                        DurationChip("60s", 60, sessionSeconds) {
                            sessionSeconds = it
                        }
                    }

                    Button(
                        onClick = {
                            showSettings = false
                            nav.popBackStack("sports", false)
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

/* ---------- CHIPS ---------- */

@Composable
private fun SpeedChip(
    label: String,
    current: String,
    onPick: () -> Unit
) {
    FilterChip(
        selected = current == label,
        onClick = onPick,
        label = { Text(label) }
    )
}

@Composable
private fun DurationChip(
    label: String,
    value: Int,
    current: Int,
    onPick: (Int) -> Unit
) {
    FilterChip(
        selected = current == value,
        onClick = { onPick(value) },
        label = { Text(label) }
    )
}
