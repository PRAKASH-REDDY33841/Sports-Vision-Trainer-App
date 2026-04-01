package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.simats.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

@Composable
fun DoctorHomeScreen(
    nav: NavController,
    email: String,
    username: String
) {
    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrown = Color(0xFF2D1E16)
    val cardBg = Color(0xFFF9F1E8)
    
    val decodedEmail = remember { URLDecoder.decode(email, "UTF-8") }
    val decodedName = remember { URLDecoder.decode(username, "UTF-8") }

    var profileUrl by remember { mutableStateOf<String?>(null) }
    val doctorName by remember { mutableStateOf(decodedName) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var notificationCount by remember { mutableIntStateOf(0) }
    var acceptedAppointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var searchAthleteQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }



    val refreshData = {
        RetrofitClient.api.getDoctorProfile(decodedEmail).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) profileUrl = response.body()?.profile_image
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
        })

        RetrofitClient.api.getAcceptedAppointments(decodedEmail).enqueue(object : Callback<AppointmentListResponse> {
            override fun onResponse(call: Call<AppointmentListResponse>, response: Response<AppointmentListResponse>) {
                if (response.isSuccessful) acceptedAppointments = response.body()?.appointments ?: emptyList()
            }
            override fun onFailure(call: Call<AppointmentListResponse>, t: Throwable) {}
        })

        RetrofitClient.api.getDoctorAppointments(decodedEmail).enqueue(object : Callback<AppointmentListResponse> {
            override fun onResponse(call: Call<AppointmentListResponse>, response: Response<AppointmentListResponse>) {
                if (response.isSuccessful) notificationCount = response.body()?.appointments?.size ?: 0
            }
            override fun onFailure(call: Call<AppointmentListResponse>, t: Throwable) {}
        })
    }

    LaunchedEffect(decodedEmail) {
        while(true) {
            refreshData()
            kotlinx.coroutines.delay(5000)
        }
    }

    Scaffold(
        bottomBar = {
            DoctorBottomBar(nav, "home", email, username)
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile + Name Column on the LEFT
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (profileUrl.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .clickable { nav.navigate("doctor_profile/$email") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(doctorName.firstOrNull()?.uppercase() ?: "D", color = Color.White, fontSize = 20.sp)
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(profileUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { nav.navigate("doctor_profile/$email") },
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Username BELOW the profile
                    Text(
                        text = doctorName,
                        fontSize = 14.sp, // Slightly smaller since it's a sub-label now
                        fontWeight = FontWeight.Bold,
                        color = darkBrown
                    )
                }

                // Notification Icon on the RIGHT with Badge
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(notificationCount.toString())
                            }
                        }
                    }
                ) {
                    IconButton(onClick = { 
                        showNotificationsDialog = true
                        notificationCount = 0 
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = darkBrown,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            if (showNotificationsDialog) {
                AppointmentNotificationDialog(
                    doctorEmail = decodedEmail,
                    onDismiss = {
                        showNotificationsDialog = false
                        refreshData()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("MORNING, CLINICIAN", fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
            Text("Welcome, Dr.\n$doctorName", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = darkBrown, lineHeight = 38.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Clinical Dashboard Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Clinical Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                    Text("Oversee athlete progress, manage treatment protocols, and review neuro-visual performance metrics in real-time.", fontSize = 12.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Your clinical expertise ensures optimal neuro-plasticity development and peak performance for every athlete.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchAthleteQuery,
                        onValueChange = { searchAthleteQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = { Text("Search athlete name...", fontSize = 12.sp) },
                        trailingIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchAthleteQuery = ""
                            }) {
                                Icon(Icons.Default.Close, null)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkBrown,
                            cursorColor = darkBrown
                        )
                    )
                } else {
                    Text("Clinical Alerts", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(Icons.Default.Search, null, tint = darkBrown)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val filteredApps = acceptedAppointments.filter {
                it.athlete_name.contains(searchAthleteQuery, ignoreCase = true)
            }

            if (filteredApps.isEmpty()) {
                Text("No matching accepted appointments", fontSize = 14.sp, color = Color.Gray)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    filteredApps.forEach { app ->
                        PatientAlertCard(
                            name = app.athlete_name,
                            profileUrl = app.profile_image,
                            time = app.time,
                            phone = app.athlete_phone,
                            onReview = {
                                nav.navigate("athlete_session_history/${app.athlete_email}/${app.athlete_name}/${app.date}")
                            },
                            onCancel = {
                                RetrofitClient.api.updateAppointmentStatus(UpdateStatusRequest(app.id, "CANCELLED"))
                                    .enqueue(object : Callback<ApiResponse> {
                                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                            refreshData()
                                        }
                                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                    })
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Important Clinical Note: Please ensure all neuro-visual thresholds are reviewed daily. Consistent monitoring of saccadic fixation and peripheral awareness is critical for athlete progress. If any significant deviations are observed, adjust the training protocols immediately. Remember to document all clinical sessions in the patient archive for future reference. Your oversight is vital to the neuro-plasticity development of our athletes.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PatientAlertCard(
    name: String,
    profileUrl: String? = null,
    time: String,
    phone: String,
    onReview: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F1E8))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Athlete Profile Image or Placeholder
            if (profileUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(profileUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = name.lowercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1E16)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Time Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = time,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Phone Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Phone,
                    null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = phone,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReview,
                    modifier = Modifier.height(44.dp).weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        "REVIEW SESSION",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.height(44.dp).weight(0.6f),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        "CANCEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorBottomBar(
    nav: NavController,
    selectedTab: String,
    email: String,
    username: String
) {
    val darkBrown = Color(0xFF2D1E16)
    val lightBlue = Color(0xFFC7E5EB)

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    if (selectedTab != "home") {
                        nav.navigate("doctor_home/$email/$username") {
                            popUpTo("doctor_home/$email/$username") { inclusive = true }
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedTab == "home") lightBlue else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painterResource(id = android.R.drawable.ic_menu_today),
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedTab == "home") darkBrown else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Home",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "home") darkBrown else Color.Gray
                )
            }

            // Settings Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    if (selectedTab != "settings") {
                        nav.navigate("doctor_settings/$email/$username")
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedTab == "settings") lightBlue else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedTab == "settings") darkBrown else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Settings",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "settings") darkBrown else Color.Gray
                )
            }
        }
    }
}

@Composable
fun AppointmentNotificationDialog(
    doctorEmail: String,
    onDismiss: () -> Unit
) {
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while(true) {
            RetrofitClient.api.getDoctorAppointments(doctorEmail).enqueue(object : Callback<AppointmentListResponse> {
                override fun onResponse(call: Call<AppointmentListResponse>, response: Response<AppointmentListResponse>) {
                    isLoading = false
                    if (response.isSuccessful) appointments = response.body()?.appointments ?: emptyList()
                }
                override fun onFailure(call: Call<AppointmentListResponse>, t: Throwable) { isLoading = false }
            })
            kotlinx.coroutines.delay(5000)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appointment Requests") },
        text = {
            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2D1E17))
                }
            } else if (appointments.isEmpty()) {
                Text("No pending requests")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(appointments) { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F1E8))
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Athlete: ${app.athlete_name}", fontWeight = FontWeight.Bold)
                                Text("Phone: ${app.athlete_phone}", fontSize = 12.sp)
                                Text("Date: ${app.date} at ${app.time.substringBeforeLast(":")}", fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            RetrofitClient.api.updateAppointmentStatus(UpdateStatusRequest(app.id, "ACCEPTED"))
                                                .enqueue(object : Callback<ApiResponse> {
                                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                                        onDismiss()
                                                    }
                                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                                })
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1E17)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Accept", color = Color.White, fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            RetrofitClient.api.updateAppointmentStatus(UpdateStatusRequest(app.id, "REJECTED"))
                                                .enqueue(object : Callback<ApiResponse> {
                                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                                        onDismiss()
                                                    }
                                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                                })
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
