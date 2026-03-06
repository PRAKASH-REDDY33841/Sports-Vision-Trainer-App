package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
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
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    }

    Scaffold { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
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