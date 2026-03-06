package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

@Composable
fun NumberReactionGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------- SETTINGS ---------- */

    var showSettings by remember { mutableStateOf(false) }
    var sessionSeconds by remember { mutableStateOf(30) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }

    /* ---------- STATE ---------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var numbers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentTarget by remember { mutableStateOf(1) }

    var score by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }
    var reactionTotal by remember { mutableStateOf(0L) }
    var lastSpawnTime by remember { mutableStateOf(0L) }

    val paused = showSettings

    /* ---------- RESET TIMER ---------- */

    LaunchedEffect(sessionSeconds) {
        timeLeft = sessionSeconds
    }

    /* ---------- TIMER ---------- */

    LaunchedEffect(showSettings, sessionSeconds) {
        while (isActive && timeLeft > 0) {
            if (!paused) {
                delay(1000)
                timeLeft--
            } else delay(200)
        }

        if (timeLeft <= 0) {

            val avg = if (score == 0) 0 else reactionTotal / score
            val finalScore = score * 120 - wrong * 40 - (avg / 5).toInt()

            nav.navigate(
                "reaction_result/$score/$avg/$wrong/$finalScore/number/$origin"
            ) {
                popUpTo("number_reaction_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- NUMBER SPAWN ENGINE ---------- */

    fun generateGrid() {
        numbers = (1..9).shuffled()
        currentTarget = 1
        lastSpawnTime = System.currentTimeMillis()
    }

    LaunchedEffect(Unit) {
        generateGrid()
    }

    /* ---------- UI ---------- */

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                modifier = Modifier.clickable { showSettings = true }
            )
        }

        /* ---------- CENTERED GRID ---------- */

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .width(260.dp),   // 👈 centered fixed width
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(numbers) { number ->

                    Box(
                        Modifier
                            .aspectRatio(1f)
                            .background(
                                if (number == currentTarget)
                                    Color.Green
                                else Color.White,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {

                                if (paused) return@clickable

                                if (number == currentTarget) {

                                    val rt =
                                        System.currentTimeMillis() - lastSpawnTime

                                    reactionTotal += rt
                                    score++
                                    currentTarget++

                                    if (currentTarget > numbers.size) {
                                        generateGrid()
                                    }

                                } else {
                                    wrong++
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            number.toString(),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    /* ---------- SETTINGS ---------- */

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
                        }

                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"
                        }

                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"
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
