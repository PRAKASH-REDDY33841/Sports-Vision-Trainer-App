package com.example.sports_vision_trainer.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sports_vision_trainer.network.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    nav: NavController,
    email: String
) {
    val decodedEmail = remember { java.net.URLDecoder.decode(email, "UTF-8") }
    val ctx = LocalContext.current
    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrown = Color(0xFF2D1E16)

    var editable by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var serverImage by remember { mutableStateOf<String?>(null) }

    var loading by remember { mutableStateOf(false) }
    var showChooser by remember { mutableStateOf(false) }

    fun reloadProfile() {
        RetrofitClient.api.getDoctorProfile(decodedEmail).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        name = it.username ?: ""
                        bio = it.bio ?: ""
                        serverImage = it.profile_image
                    }
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
        })
    }

    LaunchedEffect(decodedEmail) { reloadProfile() }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        cameraBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        cameraBitmap = bmp
        imageUri = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinical Profile", fontWeight = FontWeight.Bold) },
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
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Avatar Section
            Box(contentAlignment = Alignment.Center) {
                if (cameraBitmap != null) {
                    Image(
                        bitmap = cameraBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri ?: serverImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (editable) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { showChooser = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, darkBrown),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Change Photo", color = darkBrown)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                enabled = editable,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Medical Bio / Specialization") },
                enabled = editable,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(32.dp))

            if (!editable) {
                Button(
                    onClick = { editable = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBrown)
                ) {
                    Text("EDIT PROFILE", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        loading = true
                        uploadDoctorProfile(ctx, decodedEmail, name, bio, imageUri, cameraBitmap) { success ->
                            loading = false
                            if (success) {
                                Toast.makeText(ctx, "Profile Saved", Toast.LENGTH_SHORT).show()
                                editable = false
                                reloadProfile()
                            } else {
                                Toast.makeText(ctx, "Save Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5868))
                ) {
                    Text(if (loading) "SAVING..." else "SAVE CHANGES", fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = { editable = false; reloadProfile() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }

    if (showChooser) {
        AlertDialog(
            onDismissRequest = { showChooser = false },
            title = { Text("Update Photo") },
            text = { Text("Choose medical profile image source") },
            confirmButton = {
                TextButton(onClick = { showChooser = false; picker.launch("image/*") }) { Text("Gallery") }
            },
            dismissButton = {
                TextButton(onClick = { showChooser = false; cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Camera") }
            }
        )
    }
}

private fun uploadDoctorProfile(
    ctx: Context,
    email: String,
    name: String,
    bio: String,
    uri: Uri?,
    cameraBmp: Bitmap?,
    onDone: (Boolean) -> Unit
) {
    val file = when {
        cameraBmp != null -> {
            val f = File(ctx.cacheDir, "doc_cam.jpg")
            FileOutputStream(f).use { cameraBmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            f
        }
        uri != null -> {
            val input = ctx.contentResolver.openInputStream(uri)!!
            val f = File(ctx.cacheDir, "doc_upload.jpg")
            FileOutputStream(f).use { input.copyTo(it) }
            f
        }
        else -> null
    }

    val part = file?.let {
        val body = it.asRequestBody("image/jpeg".toMediaType())
        MultipartBody.Part.createFormData("photo", it.name, body)
    }

    RetrofitClient.api.saveDoctorProfile(
        email.toRequestBody("text/plain".toMediaType()),
        name.toRequestBody("text/plain".toMediaType()),
        bio.toRequestBody("text/plain".toMediaType()),
        part
    ).enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            onDone(response.body()?.status == "success")
        }
        override fun onFailure(call: Call<ApiResponse>, t: Throwable) { onDone(false) }
    })
}
