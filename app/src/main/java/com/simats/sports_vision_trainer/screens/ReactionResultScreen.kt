package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simats.sports_vision_trainer.model.GameSession
import com.simats.sports_vision_trainer.storage.SessionHistoryStore
import com.simats.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ReactionResultScreen(
    nav: NavController,
    email: String,
    taps: Int,
    avgReaction: Long,
    misses: Int,
    score: Int,
    source: String,
    origin: String
) {

    val ctx = LocalContext.current

    val (prevHits, prevAvg) = remember {
        SessionStatsStore.load(ctx)
    }

    val bestHits = remember {
        SessionStatsStore.loadBestHits(ctx)
    }

    /* ---------- SAVE SESSION (NEW) ---------- */

    LaunchedEffect(Unit) {

        // Old stats logic (keep)
        SessionStatsStore.save(ctx, taps, avgReaction)
        SessionStatsStore.saveBestHits(ctx, taps)

        val message =
            "Session: $taps hits • Avg $avgReaction ms • Score $score"

        NotificationCenterStore.add(ctx, message)
        NotificationBadgeStore.setNew(ctx)

        // 🔥 NEW — Save full history session
        val currentTimestamp = System.currentTimeMillis()
        val session = GameSession(
            gameType = source,
            score = score,
            avgReaction = avgReaction,
            wrong = misses,
            timestamp = currentTimestamp
        )
        SessionHistoryStore.saveSession(
            ctx,
            session
        )

        RetrofitClient.api.saveSession(
            SessionSaveRequest(
                email = email,
                gameType = source,
                score = score,
                avgReaction = avgReaction,
                wrong = misses,
                timestamp = currentTimestamp
            )
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, r: Response<ApiResponse>) {
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }

    val improvement =
        if (prevAvg > 0) prevAvg - avgReaction else 0

    val isBest = taps >= bestHits

    /* ---------- RESTART ROUTE ---------- */

    val restartRoute = when (source) {

        "jump" -> "session_start/$origin"
        "colortap" -> "color_tap_countdown/$origin"

        "swipe" -> "direction_swipe_countdown/$origin"
        "moving" -> "countdown/moving/$origin"
        "gonogo" -> "countdown/gonogo/$origin"
        "peripheral" -> "countdown/peripheral/$origin"
        "colordirection" -> "countdown/colordirection/$origin"
        "burst" -> "countdown/burst/$origin"
        "random" -> "countdown/random/$origin"
        "dualcolor" -> "countdown/dualcolor/$origin"
        "number" -> "countdown/number/$origin"
        "arrowrush" -> "countdown/arrowrush/$origin"
        "distraction" -> "countdown/distraction/$origin"
        "memory" -> "countdown/memory/$origin"
        "soundvisual" -> "countdown/soundvisual/$origin"

        else -> "sports"
    }

    /* ---------- UI ---------- */

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                "Performance Report",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Card {
                Column(Modifier.padding(16.dp)) {
                    MetricRow("Hits", taps.toString())
                    MetricRow("Misses", misses.toString())
                    MetricRow("Avg Reaction", "$avgReaction ms")
                    MetricRow("Score", score.toString())
                }
            }

            if (isBest && taps > 0) {
                AssistChip(
                    onClick = {},
                    label = { Text("🏆 New Personal Best") }
                )
            }

            if (prevAvg > 0) {
                Card {
                    Column(Modifier.padding(16.dp)) {

                        Text(
                            "Vs Previous",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        MetricRow("Previous Avg", "$prevAvg ms")

                        MetricRow(
                            "Change",
                            when {
                                improvement > 0 ->
                                    "Faster by $improvement ms"
                                improvement < 0 ->
                                    "Slower by ${-improvement} ms"
                                else -> "No change"
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    nav.navigate(restartRoute) {
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Start Again")
            }

            OutlinedButton(
                onClick = {
                    if (origin == "home") {
                        nav.popBackStack(
                            route = "home/{email}/{name}",
                            inclusive = false
                        )
                    } else {
                        nav.popBackStack(
                            route = "sports",
                            inclusive = false
                        )
                    }
                }
            ) {
                Text("Back To Home")
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}
