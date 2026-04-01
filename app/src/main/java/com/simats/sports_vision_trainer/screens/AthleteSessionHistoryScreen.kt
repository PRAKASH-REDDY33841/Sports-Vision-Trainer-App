package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simats.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AthleteSessionHistoryScreen(
    nav: NavController,
    athleteEmail: String,
    athleteName: String,
    sessionDate: String
) {
    val creamBg = Color(0xFFF9F1E8)
    val darkBrown = Color(0xFF2D1E16)
    val lightBlue = Color(0xFFC7E5EB)
    
    var sessions by remember { mutableStateOf<List<Appointment>>(emptyList()) } // Using appointment for now as simplified model
    // Actually we should use GameSession from Models
    var gameSessions by remember { mutableStateOf<List<com.simats.sports_vision_trainer.model.GameSession>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(athleteEmail) {
        RetrofitClient.api.getSessions(athleteEmail).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(call: Call<SessionResponse>, response: Response<SessionResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    gameSessions = response.body()?.sessions ?: emptyList()
                }
            }
            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    Scaffold(
        containerColor = creamBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = darkBrown)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ATHLETE SESSION HISTORY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Patient Overview
            Text("PATIENT OVERVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(
                athleteName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Session Date: $sessionDate", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = darkBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SESSION DETAILS", fontSize = 10.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Cards
            val totalScore = gameSessions.sumOf { it.score }
            val avgReaction = if (gameSessions.isNotEmpty()) gameSessions.map { it.avgReaction }.average().toInt() else 0
            val accuracy = if (gameSessions.isNotEmpty()) {
                val total = gameSessions.sumOf { it.score + it.wrong }
                if (total > 0) (gameSessions.sumOf { it.score }.toFloat() / total * 100).toInt() else 0
            } else 0

            StatCard(
                title = "TOTAL SCORE",
                value = String.format("%,d", totalScore),
                subValue = "+12% from last session",
                icon = Icons.Default.EmojiEvents,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(
                title = "AVG REACTION",
                value = "${avgReaction}ms",
                subValue = "Optimal Focus Zone",
                icon = Icons.Default.Timer,
                color = lightBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ACCURACY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Icon(Icons.Default.History, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    }
                    Text(
                        "$accuracy%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrown
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(darkBrown, RoundedCornerShape(2.dp)))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Detailed Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Detailed Breakdown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                Row {
                    Icon(Icons.Default.FilterList, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Table Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID", modifier = Modifier.weight(0.5f), fontSize = 10.sp, color = Color.Gray)
                Text("GAME TYPE", modifier = Modifier.weight(1.5f), fontSize = 10.sp, color = Color.Gray)
                Text("SCORE", modifier = Modifier.weight(1f), fontSize = 10.sp, color = Color.Gray)
                Text("AVG REACTION", modifier = Modifier.weight(1f), fontSize = 10.sp, color = Color.Gray)
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = darkBrown)
            } else {
                gameSessions.forEachIndexed { index, session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#${1000 + index}", modifier = Modifier.weight(0.5f), fontSize = 11.sp, color = Color.Gray)
                        Text(session.gameType, modifier = Modifier.weight(1.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                        Text(String.format("%,d", session.score), modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                        Text("${session.avgReaction}ms", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Clinical Note Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = darkBrown),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Doctor's Clinical Note", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "\"$athleteName shows exceptional progress in saccadic tracking speed. Reaction times have decreased compared to the baseline assessment. Recommend increasing stimulus complexity in the next cycle.\"",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Placeholder Image like the reference
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.3f
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subValue: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
            Text(
                value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1E16)
            )
            Text(subValue, fontSize = 11.sp, color = Color.Green.copy(0.7f))
        }
    }
}
