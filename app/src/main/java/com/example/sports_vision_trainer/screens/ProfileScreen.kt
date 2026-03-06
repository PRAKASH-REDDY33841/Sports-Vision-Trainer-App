package com.example.sports_vision_trainer.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sports_vision_trainer.network.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavController,
    email: String,
    username: String
) {
    val ctx = LocalContext.current

    var editable by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(username) }
    var bio by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var serverImage by remember { mutableStateOf<String?>(null) }

    var msg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showChooser by remember { mutableStateOf(false) }

    // ---------- PROFILE LOAD ----------
    fun reloadProfile() {
        RetrofitClient.api.getProfile(email)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    response.body()?.let {
                        name = it.username ?: username
                        bio = it.bio ?: ""
                        serverImage = it.profile_image
                    }
                }
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
            })
    }

    LaunchedEffect(email) { reloadProfile() }

    // ---------- GALLERY ----------
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        cameraBitmap = null
    }

    // ---------- CAMERA ----------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        cameraBitmap = bmp
        imageUri = null
    }

    // ---------- CAMERA PERMISSION ----------
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) cameraLauncher.launch(null)
        }

    // ---------- UI ----------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },

                navigationIcon = {
                    IconButton({ nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },

                // ✅ UPDATED SETTINGS NAVIGATION
                actions = {
                    IconButton({
                        nav.navigate("settings/$email/$username")
                    }) {
                        Icon(Icons.Default.Settings, null)
                    }
                }
            )
        }
    ) { pad ->

        Column(
            Modifier.padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // ---------- AVATAR ----------
            if (cameraBitmap != null) {
                Image(
                    bitmap = cameraBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp).clip(CircleShape)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUri ?: serverImage),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick = { editable = !editable }) {
                Text(if (editable) "Lock Editing" else "Edit Profile")
            }

            Spacer(Modifier.height(16.dp))

            if (editable) {
                Button(onClick = { showChooser = true }) {
                    Text("Choose Photo")
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Username") },
                enabled = editable,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                enabled = editable,
                modifier = Modifier.fillMaxWidth()
            )

            if (editable) {
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        loading = true
                        uploadProfileFinal(
                            ctx, email, name, bio,
                            imageUri, cameraBitmap
                        ) { success ->
                            loading = false
                            msg = if (success) "Saved ✓" else "Save failed ❌"
                            if (success) {
                                editable = false
                                imageUri = null
                                cameraBitmap = null
                                reloadProfile()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (loading) "Saving..." else "Save")
                }
            }

            if (msg.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(msg)
            }
        }
    }

    // ---------- CHOOSER ----------
    if (showChooser) {
        AlertDialog(
            onDismissRequest = { showChooser = false },
            title = { Text("Select Photo") },
            text = { Text("Choose image source") },
            confirmButton = {
                Column {
                    TextButton({
                        showChooser = false
                        picker.launch("image/*")
                    }) { Text("Gallery") }

                    TextButton({
                        showChooser = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }) { Text("Camera") }
                }
            },
            dismissButton = {
                TextButton({ showChooser = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
fun bitmapToFile(ctx: Context, bmp: Bitmap): File {
    val file = File(ctx.cacheDir, "camera.jpg")
    FileOutputStream(file).use {
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return file
}

fun uriToFile(ctx: Context, uri: Uri): File {
    val input = ctx.contentResolver.openInputStream(uri)!!
    val file = File(ctx.cacheDir, "upload.jpg")
    FileOutputStream(file).use { input.copyTo(it) }
    return file
}

fun uploadProfileFinal(
    ctx: Context,
    email: String,
    name: String,
    bio: String,
    uri: Uri?,
    cameraBmp: Bitmap?,
    onDone: (Boolean) -> Unit
) {
    val file = when {
        cameraBmp != null -> bitmapToFile(ctx, cameraBmp)
        uri != null -> uriToFile(ctx, uri)
        else -> null
    }

    val part = file?.let {
        val body = it.asRequestBody("image/jpeg".toMediaType())
        MultipartBody.Part.createFormData("photo", it.name, body)
    }

    RetrofitClient.api.saveProfile(
        part,
        email.toRequestBody("text/plain".toMediaType()),
        name.toRequestBody("text/plain".toMediaType()),
        bio.toRequestBody("text/plain".toMediaType())
    ).enqueue(object : Callback<ApiResponse> {

        override fun onResponse(
            call: Call<ApiResponse>,
            response: Response<ApiResponse>
        ) {
            onDone(response.body()?.status == "success")
        }

        override fun onFailure(
            call: Call<ApiResponse>,
            t: Throwable
        ) {
            onDone(false)
        }
    })
}
