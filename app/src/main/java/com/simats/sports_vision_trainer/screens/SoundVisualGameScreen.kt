package com.simats.sports_vision_trainer.screens

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

@Composable
fun SoundVisualGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------------- SETTINGS ---------------- */

    var showSettings by remember { mutableStateOf(false) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var sessionSeconds by remember { mutableIntStateOf(30) }
    var spawnDelay by remember { mutableLongStateOf(900L) }

    /* ---------------- STATE ---------------- */

    var timeLeft by remember { mutableIntStateOf(sessionSeconds) }

    var showStimulus by remember { mutableStateOf(false) }
    var stimulusColor by remember { mutableStateOf(Color.Red) }

    var score by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var reactionTotal by remember { mutableLongStateOf(0L) }
    var appearTime by remember { mutableLongStateOf(0L) }

    val paused = showSettings

    /* ---------------- RESET TIMER ---------------- */

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------------- TIMER LOOP ---------------- */

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
                score * 160 - wrong * 40 - (avg / 6).toInt()

            nav.navigate(
                "reaction_result/$score/$avg/$wrong/$finalScore/soundvisual/$origin"
            ) {
                popUpTo("sound_visual_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------------- SPAWN ENGINE ---------------- */

    LaunchedEffect(paused, spawnDelay, timeLeft) {

        while (isActive && timeLeft > 0) {

            if (paused) {
                delay(200)
                continue
            }

            delay(spawnDelay)

            stimulusColor = listOf(
                Color.Red,
                Color.Green,
                Color.Blue
            ).random()

            showStimulus = true
            appearTime = System.currentTimeMillis()

            delay(800)

            // If RED was target and user didn't tap
            if (showStimulus && stimulusColor == Color.Red) {
                wrong++
            }

            showStimulus = false
        }
    }

    /* ---------------- UI ---------------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /* ---------- TOP BAR ---------- */

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

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

            Spacer(Modifier.height(8.dp))

            // ✅ CLEAR RULE DISPLAY
            Text(
                text = "RULE: Tap ONLY RED",
                color = Color.Yellow
            )
        }

        /* ---------- STIMULUS ---------- */

        if (showStimulus) {

            Box(
                Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .background(stimulusColor, CircleShape)
                    .clickable {

                        if (paused) return@clickable

                        val rt =
                            System.currentTimeMillis() - appearTime

                        if (stimulusColor == Color.Red) {
                            score++
                            reactionTotal += rt
                        } else {
                            wrong++
                        }

                        showStimulus = false
                    }
            )
        }

        /* ---------- FOOTER ---------- */

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Score $score", color = Color.White)
            Text("Wrong $wrong", color = Color.Gray)
        }
    }

    /* ---------------- SETTINGS DIALOG ---------------- */

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
                            spawnDelay = 1300
                        }

                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"
                            spawnDelay = 900
                        }

                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"
                            spawnDelay = 550
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

/* ---------------- CHIPS ---------------- */

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
