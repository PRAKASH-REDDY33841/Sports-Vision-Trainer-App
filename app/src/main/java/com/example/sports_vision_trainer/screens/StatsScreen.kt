package com.example.sports_vision_trainer.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sports_vision_trainer.model.GameSession
import com.example.sports_vision_trainer.storage.SessionHistoryStore
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.sports_vision_trainer.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(nav: NavController, email: String) {

    val viewModel: StatsViewModel = viewModel()
    val ctx = LocalContext.current

    var sessions by remember { mutableStateOf<List<GameSession>>(emptyList()) }

    /* ---------- LOAD SESSIONS ---------- */

    LaunchedEffect(Unit) {
        sessions = SessionHistoryStore.loadSessions(ctx)

        RetrofitClient.api.getSessions(email).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(call: Call<SessionResponse>, response: Response<SessionResponse>) {
                val body = response.body()
                if (body != null && body.status == "success" && body.sessions != null) {
                    sessions = body.sessions
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                // keep showing local sessions on failure
            }
        })
    }

    val displaySessions = sessions.takeLast(30)

    val avgReaction =
        if (sessions.isNotEmpty())
            viewModel.calculateAverageReaction(sessions)
        else 0.0

    val accuracy =
        if (sessions.isNotEmpty())
            viewModel.calculateAccuracy(sessions).coerceIn(0.0, 1.0)
        else 0.0

    val bestScore = sessions.maxOfOrNull { it.score } ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF2F4F8))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            if (sessions.isEmpty()) {

                item {
                    EmptyStateUI(nav)
                }

            } else {

                item {
                    AnimatedBarGraph(displaySessions)
                }

                item {
                    KPISection(avgReaction, accuracy, bestScore)
                }

                if (sessions.size > 1) {
                    item {
                        SmoothTrendGraph(sessions)
                    }
                }

                item {
                    AchievementBadges(bestScore, accuracy)
                }

                item {
                    Text(
                        "Recent Sessions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                items(sessions.takeLast(5).reversed()) {
                    SessionHistoryCard(it)
                }
            }
        }
    }
}

/* ---------------- EMPTY STATE ---------------- */

@Composable
fun EmptyStateUI(nav: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "📊 No Statistics Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Play your first drill to unlock performance insights and analytics.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { nav.navigate("sports") }
        ) {
            Text("Start Training")
        }
    }
}

/* ---------------- BAR GRAPH ---------------- */

@Composable
fun AnimatedBarGraph(sessions: List<GameSession>) {

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = ""
    )

    Card(shape = RoundedCornerShape(24.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text("Performance Overview", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                val values = sessions.map { it.score.toFloat() }
                if (values.isEmpty()) return@Canvas

                val max = values.maxOrNull() ?: 1f
                val barWidth = size.width / (values.size * 2)

                values.forEachIndexed { i, v ->

                    val barHeight =
                        (v / max) * size.height * animatedProgress

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF6C63FF), Color(0xFFFF6584))
                        ),
                        topLeft = Offset(
                            i * barWidth * 2,
                            size.height - barHeight
                        ),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(16f, 16f)
                    )
                }
            }
        }
    }
}

/* ---------------- KPI ---------------- */

@Composable
fun KPISection(avgReaction: Double, accuracy: Double, bestScore: Int) {

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        KPIBox("Reaction", "${avgReaction.roundToInt()} ms", Modifier.weight(1f))
        KPIBox("Accuracy", "${(accuracy * 100).roundToInt()}%", Modifier.weight(1f))
        KPIBox("Best", "$bestScore", Modifier.weight(1f))
    }
}

@Composable
fun KPIBox(title: String, value: String, modifier: Modifier) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

/* ---------------- TREND GRAPH ---------------- */

@Composable
fun SmoothTrendGraph(sessions: List<GameSession>) {

    val values = sessions.takeLast(10).map { it.avgReaction.toFloat() }

    Card(shape = RoundedCornerShape(24.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text("Reaction Trend", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                if (values.size < 2) return@Canvas

                val max = values.maxOrNull()!!
                val min = values.minOrNull()!!
                val range = (max - min).coerceAtLeast(1f)

                val step = size.width / (values.size - 1)
                val path = Path()

                values.forEachIndexed { i, v ->

                    val x = i * step
                    val y = size.height -
                            ((v - min) / range) * size.height

                    if (i == 0) path.moveTo(x, y)
                    else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = Color(0xFF6C63FF),
                    style = Stroke(6f)
                )
            }
        }
    }
}

/* ---------------- ACHIEVEMENTS ---------------- */

@Composable
fun AchievementBadges(bestScore: Int, accuracy: Double) {

    Card(shape = RoundedCornerShape(24.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text("Achievements", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (bestScore > 500)
                Text("🏆 High Score Master")

            if (accuracy > 0.8)
                Text("🎯 Precision Expert")

            if (bestScore <= 500 && accuracy <= 0.8)
                Text("🔥 Keep Training!")
        }
    }
}

/* ---------------- SESSION CARD ---------------- */

@Composable
fun SessionHistoryCard(session: GameSession) {

    val formatter =
        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val date = formatter.format(Date(session.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text(
                session.gameType.uppercase(),
                fontWeight = FontWeight.Bold
            )

            Text("Score: ${session.score}")
            Text("Reaction: ${session.avgReaction} ms")
            Text("Mistakes: ${session.wrong}")

            Spacer(Modifier.height(4.dp))

            Text(date, fontSize = 12.sp, color = Color.Gray)
        }
    }
}