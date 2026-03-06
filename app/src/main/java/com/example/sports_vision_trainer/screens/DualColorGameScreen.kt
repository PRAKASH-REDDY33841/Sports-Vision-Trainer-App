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

@Composable
fun DualColorGameScreen(
    nav: NavController,
    origin: String
) {

    /* ---------- STATE ---------- */

    var score by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }

    var timeLeft by remember { mutableStateOf(30) }

    var paused by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf("MEDIUM") }
    var showStimulus by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf(Color.Red) }

    var ruleFlipped by remember { mutableStateOf(false) }

    val reactions = remember { mutableStateListOf<Long>() }
    var appearTime by remember { mutableStateOf(0L) }

    /* ---------- TIMER ---------- */

    LaunchedEffect(paused, timeLeft) {
        while (!paused && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }

        if (timeLeft == 0) {
            val avg = if (reactions.isNotEmpty())
                reactions.average().toLong() else 0L

            nav.navigate(
                "reaction_result/$score/$avg/$wrong/$score/dualcolor/$origin"
            ) {
                popUpTo("dual_color_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- RULE SWITCH ENGINE ---------- */

    LaunchedEffect(timeLeft) {
        while (timeLeft > 0) {
            delay(6000)
            ruleFlipped = !ruleFlipped
        }
    }

    /* ---------- STIMULUS ENGINE ---------- */

    fun spawnDelay(): Long {
        return when (speed) {
            "SLOW" -> 1200L
            "MEDIUM" -> 800L
            else -> 550L
        }
    }

    LaunchedEffect(paused, speed, timeLeft) {

        while (!paused && timeLeft > 0) {

            delay(spawnDelay())

            color = listOf(
                Color.Red,
                Color.Blue,
                Color.Green,
                Color.Yellow
            ).random()

            showStimulus = true
            appearTime = System.currentTimeMillis()

            delay(800)

            if (showStimulus && shouldTap(color, ruleFlipped)) {
                misses++
            }

            showStimulus = false
        }
    }

    /* ---------- UI ---------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // ===== TOP BAR =====

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TIME $timeLeft", color = Color.White)
            Text(speed, color = Color.White)

            IconButton({
                paused = true
                showSettings = true
            }) {
                Icon(Icons.Default.Settings, null, tint = Color.White)
            }
        }

        // ===== RULE INDICATOR =====

        Text(
            text = if (!ruleFlipped)
                "Tap: RED & GREEN"
            else
                "Tap: BLUE & YELLOW",
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp)
        )

        // ===== STIMULUS =====

        if (showStimulus && !paused) {

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(color, CircleShape)
                    .align(Alignment.Center)
                    .clickable {

                        val rt = System.currentTimeMillis() - appearTime
                        reactions.add(rt)

                        if (shouldTap(color, ruleFlipped)) {
                            score++
                        } else {
                            wrong++
                        }

                        showStimulus = false
                    }
            )
        }

        // ===== FOOTER =====

        Column(
            Modifier.align(Alignment.BottomCenter).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Score $score", color = Color.White)
            Text("Wrong $wrong  Miss $misses", color = Color.Gray)
        }
    }

    /* ---------- SETTINGS ---------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = {
                showSettings = false
                paused = false
            },
            confirmButton = {},
            title = { Text("Drill Settings") },
            text = {

                Column {

                    Text("Motion Speed")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SLOW","MEDIUM","FAST").forEach { s ->
                            FilterChip(
                                selected = speed == s,
                                onClick = { speed = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Duration")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(20,30,60).forEach { t ->
                            FilterChip(
                                selected = timeLeft == t,
                                onClick = { timeLeft = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { nav.popBackStack() },
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exit Drill", color = Color.White)
                    }
                }
            }
        )
    }
}

/* ---------- RULE FUNCTION ---------- */

fun shouldTap(color: Color, flipped: Boolean): Boolean {

    return if (!flipped) {
        color == Color.Red || color == Color.Green
    } else {
        color == Color.Blue || color == Color.Yellow
    }
}
