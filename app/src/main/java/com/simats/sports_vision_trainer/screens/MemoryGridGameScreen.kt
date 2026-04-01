package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
fun MemoryGridGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------------- SETTINGS ---------------- */

    var showSettings by remember { mutableStateOf(false) }
    var sessionSeconds by remember { mutableStateOf(30) }
    var motionLabel by remember { mutableStateOf("MEDIUM") }
    var flashDuration by remember { mutableStateOf(800L) }

    /* ---------------- STATE ---------------- */

    var timeLeft by remember { mutableStateOf(sessionSeconds) }
    var level by remember { mutableStateOf(1) }
    var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var userInput by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showPattern by remember { mutableStateOf(false) }

    var score by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }
    var reactionTotal by remember { mutableStateOf(0L) }
    var appearTime by remember { mutableStateOf(0L) }

    val paused = showSettings
    val gridSize = 16

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
                score * 200 - wrong * 50 - (avg / 6).toInt()

            nav.navigate(
                "reaction_result/$score/$avg/$wrong/$finalScore/memory/$origin"
            ) {
                popUpTo("memory_grid/$origin") { inclusive = true }
            }
        }
    }

    /* ---------------- GENERATE PATTERN ---------------- */

    fun generatePattern() {
        val count = 2 + level
        pattern = (0 until gridSize).shuffled().take(count)
        userInput = emptyList()
        showPattern = true
        appearTime = System.currentTimeMillis()
    }

    /* ---------------- PATTERN FLASH ---------------- */

    LaunchedEffect(level) {

        if (paused || timeLeft <= 0) return@LaunchedEffect

        generatePattern()

        delay(flashDuration)
        showPattern = false
    }

    /* ---------------- UI ---------------- */

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

        /* ---------- LEVEL ---------- */

        Text(
            "Level $level",
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(12.dp))

        /* ---------- GRID ---------- */

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            itemsIndexed(List(gridSize) { it }) { index, _ ->

                val isActive =
                    if (showPattern) pattern.contains(index)
                    else userInput.contains(index)

                Box(
                    Modifier
                        .aspectRatio(1f)
                        .background(
                            if (isActive) Color.Green else Color.DarkGray,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {

                            if (paused || showPattern) return@clickable

                            val newInput = userInput + index
                            userInput = newInput

                            if (!pattern.contains(index)) {
                                wrong++
                                userInput = emptyList()
                                level = 1
                            } else if (newInput.size == pattern.size) {

                                val rt =
                                    System.currentTimeMillis() - appearTime
                                reactionTotal += rt
                                score++
                                level++
                            }
                        }
                )
            }
        }

        /* ---------- FOOTER ---------- */

        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                            flashDuration = 1200
                        }

                        SpeedChip("MEDIUM", motionLabel) {
                            motionLabel = "MEDIUM"
                            flashDuration = 800
                        }

                        SpeedChip("FAST", motionLabel) {
                            motionLabel = "FAST"
                            flashDuration = 500
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
