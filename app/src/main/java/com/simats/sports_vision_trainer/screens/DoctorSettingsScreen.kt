package com.simats.sports_vision_trainer.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.net.URLDecoder
import com.simats.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DoctorSettingsScreen(
    nav: NavController,
    email: String,
    username: String
) {
    val ctx = LocalContext.current
    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrown = Color(0xFF2D1E16)
    val cardBg = Color(0xFFF9F1E8)
    val lightBlue = Color(0xFFC7E5EB)

    val decodedEmail = remember { URLDecoder.decode(email, "UTF-8") }
    val decodedName = remember { URLDecoder.decode(username, "UTF-8") }

    var history by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var profileUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(decodedEmail) {
        RetrofitClient.api.getDoctorProfile(decodedEmail).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) profileUrl = response.body()?.profile_image
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
        })

        RetrofitClient.api.getDoctorHistory(decodedEmail).enqueue(object : Callback<AppointmentListResponse> {
            override fun onResponse(call: Call<AppointmentListResponse>, response: Response<AppointmentListResponse>) {
                isLoading = false
                if (response.isSuccessful) history = response.body()?.appointments ?: emptyList()
            }
            override fun onFailure(call: Call<AppointmentListResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    Scaffold(
        bottomBar = { DoctorBottomBar(nav, "settings", email, username) },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text("SETTINGS", fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
            Text("Management & Support", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBrown)

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profileUrl.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(decodedName.firstOrNull()?.uppercase() ?: "D", color = Color.White, fontSize = 24.sp)
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(profileUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(decodedName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                        Text(decodedEmail, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Contact Support
            SettingsOption(
                title = "Contact Support",
                subtitle = "Open mail to get in touch",
                icon = Icons.Default.Email,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@sportsvision.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Doctor Support Request")
                    }
                    ctx.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Help & FAQs
            var showHelp by remember { mutableStateOf(false) }
            SettingsOption(
                title = "Help & FAQs",
                subtitle = "App guide and common questions",
                icon = Icons.Default.Help,
                onClick = { showHelp = !showHelp }
            )

            if (showHelp) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("How to accept appointments?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Go to Home tab and click 'Accept' on any pending alert.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text("How to view athlete progress?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Click 'REVIEW SESSION' on any accepted athlete card.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Appointment History
            Text("Recent History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBrown)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = darkBrown)
            } else if (history.isEmpty()) {
                Text("No previous appointments found.", fontSize = 14.sp, color = Color.Gray)
            } else {
                history.forEach { app ->
                    HistoryItem(app)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logout
            Button(
                onClick = { nav.navigate("role_selection") { popUpTo(0) } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout Session", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingsOption(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFF9F1E8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFF2D1E16))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D1E16))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun HistoryItem(app: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(app.athlete_name, fontWeight = FontWeight.Bold, color = Color(0xFF2D1E16))
                Text("${app.date} at ${app.time}", fontSize = 11.sp, color = Color.Gray)
            }
            StatusBadge(app.status)
        }
    }
}

