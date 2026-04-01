package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ColorDirectionGameScreen(
    nav: NavController,
    origin: String
) {

    var score by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }

    var paused by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf("MEDIUM") }

    var showStimulus by remember { mutableStateOf(false) }
    var arrow by remember { mutableStateOf("⬅") }
    var color by remember { mutableStateOf(Color.Red) }

    var combo by remember { mutableStateOf(0) }

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
                "reaction_result/$score/$avg/$wrong/$score/colordirection/$origin"
            ) {
                popUpTo("color_direction_game/$origin") { inclusive = true }
            }
        }
    }

    /* ---------- SPEED ---------- */

    fun spawnDelay(): Long {
        val base = when (speed) {
            "SLOW" -> 1300L
            "MEDIUM" -> 900L
            else -> 650L
        }
        val ramp = (score * 10).coerceAtMost(300)
        return (base - ramp).coerceAtLeast(350)
    }

    /* ---------- LOOP ---------- */

    LaunchedEffect(paused, speed, timeLeft) {

        while (!paused && timeLeft > 0) {

            delay(spawnDelay())

            arrow = listOf("⬅","➡","⬆","⬇").random()
            color = listOf(
                Color.Red,
                Color.Green,
                Color.Blue,
                Color.Yellow
            ).random()

            showStimulus = true
            appearTime = System.currentTimeMillis()

            delay(850)

            if (showStimulus) {
                misses++
                combo = 0
            }

            showStimulus = false
        }
    }

    fun expected(): String =
        when (color) {
            Color.Red -> "⬅"
            Color.Green -> "➡"
            Color.Blue -> "⬆"
            else -> "⬇"
        }

    /* ---------- UI ---------- */

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(showStimulus) {

                detectTapGestures(
                    onTap = {
                        if (!showStimulus || paused) return@detectTapGestures

                        val rt =
                            System.currentTimeMillis() - appearTime
                        reactions.add(rt)

                        if (arrow == expected()) {
                            score += (1 + combo / 3)
                            combo++
                        } else {
                            wrong++
                            combo = 0
                        }

                        showStimulus = false
                    }
                )
            }
    ) {

        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
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

        Card(
            colors = CardDefaults.cardColors(Color(0x22FFFFFF)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            Column(Modifier.padding(10.dp)) {
                Text("RULE", color = Color.White, fontWeight = FontWeight.Bold)
                Text("🔴 Red = LEFT", color = Color.Red)
                Text("🟢 Green = RIGHT", color = Color.Green)
                Text("🔵 Blue = UP", color = Color.Cyan)
                Text("🟡 Yellow = DOWN", color = Color.Yellow)
            }
        }

        if (showStimulus && !paused) {

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(color),
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.Center)
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        arrow,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Column(
            Modifier.align(Alignment.BottomCenter).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Score $score", color = Color.White)
            Text("Wrong $wrong  Miss $misses", color = Color.Gray)
            if (combo >= 3)
                Text("COMBO x$combo", color = Color.Yellow)
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
            containerColor = Color(0xFFEDE2D9),
            shape = RoundedCornerShape(28.dp),
            title = { Text("Drill Settings") },
            text = {

                Column {

                    Text("Motion Speed")
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("SLOW","MEDIUM","FAST").forEach { s ->
                            FilterChip(
                                selected = speed == s,
                                onClick = { speed = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text("Duration")
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(20,30,60).forEach { t ->
                            FilterChip(
                                selected = timeLeft == t,
                                onClick = { timeLeft = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    /* ✅ FIXED EXIT DRILL */

                    Button(
                        onClick = {
                            nav.navigate("sports") {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Exit Drill", color = Color.White)
                    }
                }
            }
        )
    }
}
