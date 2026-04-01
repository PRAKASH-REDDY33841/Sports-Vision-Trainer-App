package com.simats.sports_vision_trainer.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import java.util.*

/* ------------------------------------------------ */
/* ---------------- STEP MODELS ------------------- */
/* ------------------------------------------------ */

enum class StepType {
    MOVE,
    CIRCLE_CW,
    CIRCLE_CCW,
    ZOOM_IN,
    ZOOM_OUT,
    BLINK,
    HOLD
}

data class ExerciseStep(
    val instruction: String,
    val type: StepType,
    val moveX: Float = 0f,
    val moveY: Float = 0f,
    val duration: Int
)

/* ------------------------------------------------ */
/* ---------------- MAIN CONTROLLER ---------------- */
/* ------------------------------------------------ */

@Composable
fun ExerciseScreen(navController: NavController) {

    var started by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var totalTime by remember { mutableStateOf("00:00") }

    when {
        completed -> {
            ExerciseCompleteScreen(totalTime = totalTime) {
                navController.popBackStack()
            }
        }

        started -> {
            EyeExercisePlayer(
                onExerciseComplete = { time ->
                    totalTime = time
                    completed = true
                },
                onBack = { started = false }
            )
        }

        else -> {
            ExerciseIntroScreen(
                onStart = { started = true },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/* ------------------------------------------------ */
/* ---------------- INTRO SCREEN ------------------ */
/* ------------------------------------------------ */

@Composable
fun ExerciseIntroScreen(onStart: () -> Unit, onBack: () -> Unit) {

    val calendar = Calendar.getInstance()
    val todayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1

    val days = listOf("Su","Mo","Tu","We","Th","Fr","Sa")

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF0F0F0F), Color.Black)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
            .clickable { onStart() }
    ) {

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(60.dp))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "EYES RECOVER WORKOUT",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(80.dp))

            PulsingEye()

            Spacer(Modifier.height(60.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                days.forEachIndexed { index, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            Modifier
                                .height(if (index == todayIndex) 50.dp else 40.dp)
                                .width(18.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (index == todayIndex)
                                        Color(0xFF6FCF97)
                                    else
                                        Color.LightGray.copy(alpha = 0.4f)
                                )
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            day,
                            color = if (index == todayIndex)
                                Color(0xFF6FCF97)
                            else
                                Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                "[ TOUCH SCREEN TO START EXERCISES ]",
                color = Color.Gray
            )
        }
    }
}

/* ------------------------------------------------ */
/* ---------------- PULSING EYE ------------------- */
/* ------------------------------------------------ */

@Composable
fun PulsingEye() {

    val infinite = rememberInfiniteTransition(label = "")

    val scale by infinite.animateFloat(
        1f,
        1.1f,
        infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        Modifier
            .size(180.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFFFFB36B), Color(0xFFFF7A00))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("👁", fontSize = 60.sp)
    }
}

/* ------------------------------------------------ */
/* ---------------- EXERCISE PLAYER ---------------- */
/* ------------------------------------------------ */

@Composable
fun EyeExercisePlayer(
    onExerciseComplete: (String) -> Unit,
    onBack: () -> Unit
) {

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    var instruction by remember { mutableStateOf("") }

    val steps = listOf(
        ExerciseStep("Look Left", StepType.MOVE, -140f, 0f, 3000),
        ExerciseStep("Look Right", StepType.MOVE, 140f, 0f, 3000),
        ExerciseStep("Look Up", StepType.MOVE, 0f, -140f, 3000),
        ExerciseStep("Look Down", StepType.MOVE, 0f, 140f, 3000),
        ExerciseStep("Rotate Clockwise", StepType.CIRCLE_CW, duration = 6000),
        ExerciseStep("Rotate Anti-Clockwise", StepType.CIRCLE_CCW, duration = 6000),
        ExerciseStep("Focus Center", StepType.MOVE, 0f, 0f, 3000),
        ExerciseStep("Zoom In", StepType.ZOOM_IN, duration = 2000),
        ExerciseStep("Zoom Out", StepType.ZOOM_OUT, duration = 2000),
        ExerciseStep("Blink Rapidly", StepType.BLINK, duration = 4000),
        ExerciseStep("Close Eyes & Relax", StepType.HOLD, duration = 5000)
    )

    val totalDuration = steps.sumOf { it.duration }

    LaunchedEffect(Unit) {

        for (step in steps) {

            instruction = step.instruction

            when (step.type) {

                StepType.MOVE -> {
                    offsetX.animateTo(step.moveX, tween(step.duration))
                    offsetY.animateTo(step.moveY, tween(step.duration))
                }

                StepType.CIRCLE_CW -> {
                    val radius = 110f
                    val frames = 60
                    repeat(frames) { i ->
                        val angle = (i * 6) * Math.PI / 180
                        offsetX.snapTo((radius * cos(angle)).toFloat())
                        offsetY.snapTo((radius * sin(angle)).toFloat())
                        delay(step.duration / frames.toLong())
                    }
                }

                StepType.CIRCLE_CCW -> {
                    val radius = 110f
                    val frames = 60
                    repeat(frames) { i ->
                        val angle = (-i * 6) * Math.PI / 180
                        offsetX.snapTo((radius * cos(angle)).toFloat())
                        offsetY.snapTo((radius * sin(angle)).toFloat())
                        delay(step.duration / frames.toLong())
                    }
                }

                StepType.ZOOM_IN -> {
                    scale.animateTo(1.4f, tween(step.duration))
                }

                StepType.ZOOM_OUT -> {
                    scale.animateTo(1f, tween(step.duration))
                }

                StepType.BLINK -> {
                    repeat(6) {
                        alpha.animateTo(0f, tween(200))
                        alpha.animateTo(1f, tween(200))
                    }
                }

                StepType.HOLD -> delay(step.duration.toLong())
            }
        }

        val minutes = totalDuration / 60000
        val seconds = (totalDuration % 60000) / 1000
        val formatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

        onExerciseComplete(formatted)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            text = instruction,
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(140.dp)
                    .offset(offsetX.value.dp, offsetY.value.dp)
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        alpha = alpha.value
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFB36B), Color(0xFFFF7A00))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("👁", fontSize = 50.sp)
            }
        }
    }
}
/* ------------------------------------------------ */
/* --------------- COMPLETION SCREEN -------------- */
/* ------------------------------------------------ */

@Composable
fun ExerciseCompleteScreen(
    totalTime: String,
    onDone: () -> Unit
) {

    var notifyEnabled by remember { mutableStateOf(true) }
    var timesPerDay by remember { mutableStateOf(1) }

    val calendar = Calendar.getInstance()
    val todayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1

    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF111111),
            Color(0xFF1A1A1A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Exercises completed!",
                color = Color.White,
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Perform these exercises at least once a day",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF222222)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total time:",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalTime,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val days = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                days.forEachIndexed { index, day ->

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .width(30.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (index == todayIndex)
                                        Color(0xFF6FCF97)
                                    else
                                        Color(0xFFCDD5CF)
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = day,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Notification",
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = notifyEnabled,
                    onCheckedChange = { notifyEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How many times a day do you plan to exercise?",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "-",
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.clickable {
                        if (timesPerDay > 1) timesPerDay--
                    }
                )

                Spacer(modifier = Modifier.width(40.dp))

                Text(
                    text = timesPerDay.toString(),
                    fontSize = 28.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(40.dp))

                Text(
                    text = "+",
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.clickable {
                        timesPerDay++
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(text = "OK")
            }
        }
    }
}
