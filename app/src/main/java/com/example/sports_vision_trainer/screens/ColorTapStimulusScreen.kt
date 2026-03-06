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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun ColorTapStimulusScreen(
    nav: NavController,
    origin: String
) {

    val ctx = LocalContext.current

    // ---------- SETTINGS ----------
    var showSettings by remember { mutableStateOf(false) }

    var sessionSeconds by remember { mutableIntStateOf(30) }
    var speedLabel by remember { mutableStateOf("MEDIUM") }
    var minDelayMs by remember { mutableLongStateOf(700L) }
    var maxDelayMs by remember { mutableLongStateOf(1500L) }

    // ---------- STATE ----------
    var timeLeft by remember { mutableIntStateOf(30) }
    var dots by remember { mutableStateOf<List<StimulusDot>>(emptyList()) }
    var visible by remember { mutableStateOf(false) }
    var lastSpawn by remember { mutableLongStateOf(0L) }

    var taps by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var reactionTotal by remember { mutableLongStateOf(0L) }

    val tapPoints = remember { mutableListOf<Pair<Float, Float>>() }

    val targetColor = Color.Red
    val distractorColors = listOf(Color.Green, Color.Blue, Color.Yellow)

    // ---------- RESET TIMER ----------
    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    // ---------- TIMER ----------
    LaunchedEffect(showSettings, sessionSeconds) {

        while (isActive && timeLeft > 0) {
            if (!showSettings) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {

            val avg = if (taps == 0) 0 else reactionTotal / taps

            val score =
                taps * 120 -
                        misses * 50 -
                        (avg / 6).toInt()

            HeatmapStore.save(ctx, tapPoints)

            nav.navigate("reaction_result/$taps/$avg/$misses/$score/colortap/$origin")
            {
                popUpTo("color_tap/$origin") { inclusive = true }
            }
        }
    }

    // ---------- STIMULUS ENGINE ----------
    LaunchedEffect(visible, showSettings, timeLeft, minDelayMs, maxDelayMs) {

        if (showSettings || timeLeft <= 0 || visible) return@LaunchedEffect

        delay(Random.nextLong(minDelayMs, maxDelayMs))

        if (showSettings || timeLeft <= 0) return@LaunchedEffect

        val target = StimulusDot(
            Random.nextFloat(),
            Random.nextFloat(),
            targetColor,
            true
        )

        val distractors = List(3) {
            StimulusDot(
                Random.nextFloat(),
                Random.nextFloat(),
                distractorColors.random(),
                false
            )
        }

        dots = distractors + target
        lastSpawn = System.currentTimeMillis()
        visible = true
    }

    // ---------- UI ----------
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TIME $timeLeft", color = Color.White)

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

            Spacer(Modifier.height(6.dp))

            Text("Focus on RED • Tap RED only", color = Color.Red)

            Text(
                "Hits $taps   Misses $misses",
                color = Color.White
            )
        }

        if (visible) {
            BoxWithConstraints(Modifier.fillMaxSize()) {

                dots.forEach { dot ->

                    Box(
                        Modifier
                            .offset(
                                x = maxWidth * dot.x - 26.dp,
                                y = maxHeight * dot.y - 26.dp
                            )
                            .size(52.dp)
                            .background(dot.color, CircleShape)
                            .clickable {

                                if (dot.isTarget) {
                                    val rt =
                                        System.currentTimeMillis() - lastSpawn
                                    reactionTotal += rt
                                    taps++
                                    tapPoints.add(dot.x to dot.y)
                                } else {
                                    misses++
                                }

                                visible = false
                            }
                    )
                }
            }
        }
    }

    // ---------- SETTINGS DIALOG ----------
    if (showSettings) {

        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    Text("Duration")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        LocalDurationChip("15s", 15, sessionSeconds) {
                            sessionSeconds = it
                        }

                        LocalDurationChip("30s", 30, sessionSeconds) {
                            sessionSeconds = it
                        }

                        LocalDurationChip("60s", 60, sessionSeconds) {
                            sessionSeconds = it
                        }
                    }

                    Text("Stimulus Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        LocalSpeedChip("SLOW", speedLabel) {
                            speedLabel = "SLOW"
                            minDelayMs = 1100
                            maxDelayMs = 1900
                        }

                        LocalSpeedChip("MEDIUM", speedLabel) {
                            speedLabel = "MEDIUM"
                            minDelayMs = 700
                            maxDelayMs = 1500
                        }

                        LocalSpeedChip("FAST", speedLabel) {
                            speedLabel = "FAST"
                            minDelayMs = 350
                            maxDelayMs = 800
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

/* ---------------- LOCAL CHIPS ---------------- */

@Composable
private fun LocalDurationChip(
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

@Composable
private fun LocalSpeedChip(
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
