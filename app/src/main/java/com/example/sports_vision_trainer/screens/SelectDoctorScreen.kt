package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.Calendar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sports_vision_trainer.R
import com.example.sports_vision_trainer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDoctorScreen(
    nav: NavController,
    athleteEmail: String,
    athleteName: String
) {
    val decodedEmail = remember { java.net.URLDecoder.decode(athleteEmail, "UTF-8") }
    val decodedName = remember { java.net.URLDecoder.decode(athleteName, "UTF-8") }
    
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    var selectedDoctorForBooking by remember { mutableStateOf<Doctor?>(null) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val filteredDoctors = doctors.filter {
        it.full_name.contains(searchQuery, ignoreCase = true) ||
        it.hospital_name.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        RetrofitClient.api.getDoctors().enqueue(object : Callback<DoctorListResponse> {
            override fun onResponse(call: Call<DoctorListResponse>, response: Response<DoctorListResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    doctors = response.body()?.doctors ?: emptyList()
                } else {
                    errorMessage = "Failed to load doctors"
                }
            }

            override fun onFailure(call: Call<DoctorListResponse>, t: Throwable) {
                isLoading = false
                errorMessage = t.message
            }
        })
    }

    if (showBookingDialog && selectedDoctorForBooking != null) {
        BookingDialog(
            doctor = selectedDoctorForBooking!!,
            athleteEmail = decodedEmail,
            athleteName = decodedName,
            onDismiss = { showBookingDialog = false },
            onBooked = {
                showBookingDialog = false
                showSuccessDialog = true
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Booking Sent") },
            text = { Text("your appointment has been sended please waait for the response") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1E17))
                ) {
                    Text("Back to Page", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name or hospital...") },
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Text(
                            "SELECT DOCTOR", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isSearching) {
                            isSearching = false
                            searchQuery = ""
                        } else {
                            nav.popBackStack() 
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(
                            if (isSearching) Icons.Default.Search else Icons.Default.Search, 
                            contentDescription = "Search", 
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFDF8F3)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isSearching) {
                Text(
                    text = "Elite Vision Specialists",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D1E17)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Connect with the world's leading sports ophthalmologists and performance vision coaches.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2D1E17))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $errorMessage", color = Color.Red)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(doctor, onBookClick = {
                            selectedDoctorForBooking = doctor
                            showBookingDialog = true
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F1E8))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    if (doctor.profile_image != null) {
                        Image(
                            painter = rememberAsyncImagePainter(doctor.profile_image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Dr. ${doctor.full_name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1E17)
                    )
                    Text(
                        text = doctor.medical_license.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = doctor.hospital_name, fontSize = 13.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_email),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = doctor.clinic_email, fontSize = 13.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1E17))
            ) {
                Text("Book", color = Color.White)
            }
        }
    }
}

@Composable
fun BookingDialog(
    doctor: Doctor,
    athleteEmail: String,
    athleteName: String,
    onDismiss: () -> Unit,
    onBooked: () -> Unit
) {
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }
    
    val ctx = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        ctx,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val formattedMonth = (month + 1).toString().padStart(2, '0')
            val formattedDay = day.toString().padStart(2, '0')
            date = "$year-$formattedMonth-$formattedDay"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    val timePickerDialog = TimePickerDialog(
        ctx,
        { _: TimePicker, hour: Int, minute: Int ->
            val formattedHour = hour.toString().padStart(2, '0')
            val formattedMinute = minute.toString().padStart(2, '0')
            time = "$formattedHour:$formattedMinute"
            timeError = null
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val dateInteractionSource = remember { MutableInteractionSource() }
    if (dateInteractionSource.collectIsPressedAsState().value) {
        datePickerDialog.show()
    }

    val timeInteractionSource = remember { MutableInteractionSource() }
    if (timeInteractionSource.collectIsPressedAsState().value) {
        timePickerDialog.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Appointment with Dr. ${doctor.full_name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    readOnly = true,
                    interactionSource = dateInteractionSource,
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Select Date") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1E17),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { },
                    readOnly = true,
                    interactionSource = timeInteractionSource,
                    label = { Text("Time (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Select Time") },
                    isError = timeError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1E17),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                if (timeError != null) {
                    Text(
                        text = timeError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input -> 
                        val digits = input.filter { it.isDigit() }
                        phone = digits
                        phoneError = if (digits.length != 10 && digits.isNotEmpty()) {
                            "Mobile number must be exactly 10 digits"
                        } else null
                    },
                    label = { Text("Mobile Number") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("10-digit mobile number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = phoneError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1E17),
                    )
                )
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    timeError = null
                    var isTimeValid = true
                    if (date.isNotBlank() && time.isNotBlank()) {
                        try {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            val selectedDateTime = sdf.parse("$date $time")
                            if (selectedDateTime != null && selectedDateTime.before(java.util.Calendar.getInstance().time)) {
                                isTimeValid = false
                                timeError = "Please select a future time"
                            }
                        } catch (e: Exception) {
                            // ignore parsing errors
                        }
                    }

                    if (isTimeValid && date.isNotBlank() && time.isNotBlank() && phone.length == 10 && phoneError == null) {
                        val req = AppointmentRequest(
                            doctor_email = doctor.clinic_email,
                            athlete_email = athleteEmail,
                            athlete_name = athleteName,
                            athlete_phone = phone,
                            date = date,
                            time = time
                        )
                        RetrofitClient.api.bookAppointment(req).enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                if (response.isSuccessful) onBooked()
                            }
                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                        })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1E17))
            ) {
                Text("Confirm Booking", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
