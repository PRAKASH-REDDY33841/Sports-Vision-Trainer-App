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
import kotlin.random.Random

data class DistractionDot(
    val x: Float,
    val y: Float,
    val isTarget: Boolean
)

@Composable
fun DistractionLayerGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------------- SETTINGS ---------------- */

    var showSettings by remember { mutableStateOf(false) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var sessionSeconds by remember { mutableStateOf(30) }
    var spawnDelay by remember { mutableStateOf(900L) }

    /* ---------------- STATE ---------------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var dots by remember { mutableStateOf<List<DistractionDot>>(emptyList()) }

    var score by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(0) }

    var reactionTotal by remember { mutableStateOf(0L) }
    var lastSpawnTime by remember { mutableStateOf(0L) }

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
                score * 150 +
                        combo * 10 -
                        wrong * 40 -
                        (avg / 6).toInt()

            nav.navigate(
                "reaction_result/$score/$avg/$wrong/$finalScore/distraction/$origin"
            ) {
                popUpTo("distraction_layer/$origin") { inclusive = true }
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

            val target = DistractionDot(
                Random.nextFloat(),
                Random.nextFloat(),
                true
            )

            val distractors = List(6) {
                DistractionDot(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    false
                )
            }

            dots = distractors + target
            lastSpawnTime = System.currentTimeMillis()
        }
    }

    /* ---------------- UI ---------------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

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

        BoxWithConstraints(Modifier.fillMaxSize()) {

            dots.forEach { dot ->

                Box(
                    Modifier
                        .offset(
                            x = maxWidth * dot.x - 20.dp,
                            y = maxHeight * dot.y - 20.dp
                        )
                        .size(40.dp)
                        .background(
                            if (dot.isTarget) Color.Red else Color.Gray,
                            CircleShape
                        )
                        .clickable {

                            if (paused) return@clickable

                            if (dot.isTarget) {

                                val rt =
                                    System.currentTimeMillis() - lastSpawnTime

                                reactionTotal += rt
                                combo++
                                score += (1 + combo / 3)

                            } else {
                                wrong++
                                combo = 0
                            }
                        }
                )
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Score $score", color = Color.White)
            Text("Wrong $wrong", color = Color.Gray)

            if (combo >= 3)
                Text("COMBO x$combo", color = Color.Yellow)
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

/* ---------------- CHIP COMPONENTS ---------------- */

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
