package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.simats.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.delay
import android.content.Context

@Composable
fun HomeScreen(
    nav: NavController,
    email: String,
    username: String
) {


    val ctx = LocalContext.current

    var hasNew by remember { mutableStateOf(NotificationBadgeStore.hasNew(ctx)) }
    val notifications by remember { mutableStateOf(NotificationCenterStore.load(ctx)) }

    var profileUrl by remember { mutableStateOf<String?>(null) }
    var showNotifications by remember { mutableStateOf(false) }
    var appointmentAlert by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(email) {
        RetrofitClient.api.getProfile(email)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    profileUrl = response.body()?.profile_image
                }

                override fun onFailure(
                    call: Call<ProfileResponse>,
                    t: Throwable
                ) {
                    println("PROFILE LOAD FAILED = ${t.message}")
                }
            })

        val prefs = ctx.getSharedPreferences("Notifications", Context.MODE_PRIVATE)
        
        while (true) {
            RetrofitClient.api.getAthleteNotifications(email).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val body = response.body()
                        val appId = body?.id ?: -1
                        val appStatus = body?.app_status ?: ""
                        val msg = body?.message ?: ""
                        
                        val lastNotifiedId = prefs.getInt("last_id", -1)
                        val lastNotifiedStatus = prefs.getString("last_status", "")
                        val lastNotifiedTime = prefs.getLong("last_time", 0)
                        
                        if (appStatus == "REJECTED") {
                            // Notify once for a specific rejected appointment
                            if (appId != lastNotifiedId || lastNotifiedStatus != "REJECTED") {
                                NotificationHelper.showNotification(ctx, "Appointment Rejected", msg)
                                prefs.edit()
                                    .putInt("last_id", appId)
                                    .putString("last_status", "REJECTED")
                                    .apply()
                            }
                        } else if (appStatus == "ACCEPTED") {
                            val now = System.currentTimeMillis()
                            // Remind every hour if accepted
                            if (appId != lastNotifiedId || lastNotifiedStatus != "ACCEPTED" || (now - lastNotifiedTime > 3600000)) {
                                NotificationHelper.showNotification(ctx, "Appointment Accepted", msg)
                                prefs.edit()
                                    .putInt("last_id", appId)
                                    .putString("last_status", "ACCEPTED")
                                    .putLong("last_time", now)
                                    .apply()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
            })
            delay(60000) // Poll every 1 minute
        }
    }

    val scrollState = rememberScrollState()

    Scaffold { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (profileUrl.isNullOrBlank()) {
                    Box(
                        Modifier.size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB07A4A))
                            .clickable {
                                nav.navigate("profile/$email/$username")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            username.first().uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(profileUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                nav.navigate("profile/$email/$username")
                            }
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Box {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showNotifications = true
                            NotificationBadgeStore.clear(ctx)
                            hasNew = false
                        }
                    )

                    if (hasNew) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text("HOME DASHBOARD", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("LEVEL 1", color = Color.Gray)

            Spacer(Modifier.height(28.dp))

            Text("DAILY TRAINING", color = Color.Gray)

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    Modifier.size(210.dp)
                        .background(Color(0xFFE4E6EB), CircleShape)
                )

                Box(
                    Modifier.size(150.dp)
                        .background(Color(0xFFD0D4DC), CircleShape)
                )

                FloatingActionButton(
                    onClick = { nav.navigate("session_start/home") },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "START SESSION",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        nav.navigate("session_start/home")
                    }
            )

            Text(
                "Estimated duration: 5 mins",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                FeatureCard(
                    "Color Tap", "A",
                    Color(0xFFE8D2C4), true,
                    Modifier.weight(1f).clickable {
                        nav.navigate("color_tap_countdown/home")
                    }
                )

                FeatureCard(
                    "Direction\nSwipe", "↔",
                    Color(0xFFE6E7ED), false,
                    Modifier.weight(1f).clickable {
                        nav.navigate("direction_swipe_countdown/home")
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            // Schedule Appointment Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE4E6EB))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Schedule Appointment with Doctors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1E17),
                        lineHeight = 24.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        "Review your vision baseline and customize your training protocol for the upcoming season.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                            val encodedName = java.net.URLEncoder.encode(username, "UTF-8")
                            nav.navigate("select_doctor/$encodedEmail/$encodedName")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1E17))
                    ) {
                        Text("BOOK SESSION", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))

            // Project Info Text (Added for space and context)
            Text(
                "Elevate Your Game",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sports vision training is the specialized practice of improving an athlete's visual performance through targeted exercises. By training the brain-eye connection, athletes can improve reaction time, depth perception, and peripheral awareness.",
                fontSize = 13.sp,
                color = Color.LightGray,
                lineHeight = 18.sp
            )
            
            Spacer(Modifier.height(80.dp)) // Extra space for fixed bottom bar
        }
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            confirmButton = {
                TextButton({ showNotifications = false }) {
                    Text("Close")
                }
            },
            title = { Text("Notifications") },
            text = {
                LazyColumn {
                    items(notifications) { msg ->
                        Text(msg, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: String,
    bg: Color,
    border: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = if (border) BorderStroke(1.dp, Color.Black) else null
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(title)
        }
    }
}