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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GoNoGoFlashGameScreen(
    nav: NavController,
    origin: String   // ✅ Added origin
) {

    /* ---------------- STATE ---------------- */

    var score by remember { mutableStateOf(0) }
    var misses by remember { mutableStateOf(0) }
    var impulseErrors by remember { mutableStateOf(0) }

    var timeLeft by remember { mutableStateOf(30) }
    var paused by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf("MEDIUM") }

    var showStimulus by remember { mutableStateOf(false) }
    var isGo by remember { mutableStateOf(true) }

    val reactions = remember { mutableStateListOf<Long>() }
    var appearTime by remember { mutableStateOf(0L) }

    /* ---------------- TIMER LOOP ---------------- */

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            if (!paused) timeLeft--
        }

        val avg = if (reactions.isNotEmpty())
            reactions.average().toLong() else 0L

        // ✅ UPDATED RESULT ROUTE (origin added)
        nav.navigate(
            "reaction_result/$score/$avg/$impulseErrors/$score/gonogo/$origin"
        ) {
            popUpTo("gonogo_game/$origin") { inclusive = true }
        }
    }

    /* ---------------- FLASH ENGINE ---------------- */

    LaunchedEffect(Unit) {

        while (true) {

            if (paused || timeLeft <= 0) {
                delay(100)
                continue
            }

            val baseDelay = when (speed) {
                "SLOW" -> 1200L
                "MEDIUM" -> 800L
                else -> 500L
            }

            delay(baseDelay + Random.nextLong(200, 700))

            if (paused || timeLeft <= 0) continue

            isGo = Random.nextFloat() > 0.4f
            showStimulus = true
            appearTime = System.currentTimeMillis()

            delay(550)

            if (showStimulus && isGo) misses++

            showStimulus = false
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
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TIME $timeLeft", color = Color.White)
            Text(speed, color = Color.White)

            IconButton(
                onClick = {
                    paused = true
                    showSettings = true
                }
            ) {
                Icon(Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White)
            }
        }

        if (showStimulus && !paused) {

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGo)
                        Color.Green else Color.Red
                ),
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.Center)
                    .clickable {

                        val rt =
                            System.currentTimeMillis() - appearTime

                        if (isGo) {
                            score++
                            reactions.add(rt)
                        } else {
                            impulseErrors++
                        }

                        showStimulus = false
                    }
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isGo) "GO" else "NO GO",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Score $score", color = Color.Gray)
            Text("Miss $misses", color = Color.Gray)
            Text("Impulse $impulseErrors", color = Color.Gray)
        }
    }

    /* ---------------- SETTINGS PANEL ---------------- */

    if (showSettings) {

        AlertDialog(
            onDismissRequest = {
                showSettings = false
                paused = false
            },
            confirmButton = {},
            containerColor = Color(0xFFEDE2D9),
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Drill Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {

                Column {

                    Text("Motion Speed")
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement =
                    Arrangement.spacedBy(10.dp)) {

                        listOf("SLOW","MEDIUM","FAST")
                            .forEach { s ->

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

                    Row(horizontalArrangement =
                    Arrangement.spacedBy(10.dp)) {

                        listOf(20,30,60).forEach { t ->
                            FilterChip(
                                selected = timeLeft == t,
                                onClick = { timeLeft = t },
                                label = { Text("${t}s") }
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    Button(
                        onClick = { nav.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Exit Drill",
                            color = Color.White)
                    }
                }
            }
        )
    }
}
