package com.example.sports_vision_trainer.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    nav: NavController,
    athleteEmail: String
) {
    val decodedEmail = remember { java.net.URLDecoder.decode(athleteEmail, "UTF-8") }
    val ctx = LocalContext.current
    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrown = Color(0xFF2D1E16)

    var bookings by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Previous

    fun loadBookings() {
        isLoading = true
        RetrofitClient.api.getAthleteBookings(decodedEmail).enqueue(object : Callback<AppointmentListResponse> {
            override fun onResponse(call: Call<AppointmentListResponse>, response: Response<AppointmentListResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    bookings = response.body()?.bookings ?: emptyList()
                }
            }
            override fun onFailure(call: Call<AppointmentListResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    LaunchedEffect(decodedEmail) { loadBookings() }

    val filteredBookings = bookings.filter { b ->
        if (selectedTab == 0) {
            b.status == "PENDING" || b.status == "ACCEPTED"
        } else {
            b.status == "REJECTED" || b.status == "CANCELLED" || b.status == "COMPLETED"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            
            // Tab Buttons
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).background(Color(0xFFE4E6EB), RoundedCornerShape(24.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) darkBrown else Color.Transparent,
                        contentColor = if (selectedTab == 0) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = null
                ) {
                    Text("Active Booking", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) darkBrown else Color.Transparent,
                        contentColor = if (selectedTab == 1) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = null
                ) {
                    Text("Previous Booking", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = darkBrown)
                }
            } else if (filteredBookings.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bookings found", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredBookings) { booking ->
                        BookingItem(
                            booking = booking,
                            onCancel = { id ->
                                RetrofitClient.api.cancelAppointment(id).enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            Toast.makeText(ctx, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                                            loadBookings()
                                        }
                                    }
                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Appointment, onCancel: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.doctor_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                StatusBadge(booking.status)
            }
            Spacer(Modifier.height(8.dp))
            Text("Date: ${booking.date}", fontSize = 14.sp, color = Color.Gray)
            Text("Time: ${booking.time.substringBeforeLast(":")}", fontSize = 14.sp, color = Color.Gray)
            
            if (booking.status == "PENDING" || booking.status == "ACCEPTED") {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onCancel(booking.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel Appointment", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "ACCEPTED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
        "CANCELLED" -> Color(0xFF9E9E9E)
        "PENDING" -> Color(0xFFFF9800)
        else -> Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
