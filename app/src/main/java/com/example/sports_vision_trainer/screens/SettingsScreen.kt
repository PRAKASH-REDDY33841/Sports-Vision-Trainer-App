package com.example.sports_vision_trainer.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController,
    email: String,
    username: String,
    onDarkModeChange: (Boolean) -> Unit
) {

    val ctx = LocalContext.current

    var dark by remember { mutableStateOf(SettingsStore.darkMode) }
    var audioDefault by remember { mutableStateOf(SettingsStore.audioEnabled) }
    var hapticDefault by remember { mutableStateOf(SettingsStore.hapticEnabled) }
    var showHelp by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton({
                        nav.navigate("profile/$email/$username") {
                            popUpTo("settings/$email/$username") {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text("General", style = MaterialTheme.typography.titleLarge)

            HorizontalDivider()

            // 🌙 DARK MODE
            SwitchRow("Dark Mode", dark) {
                dark = it
                onDarkModeChange(it)
                SettingsStore.darkMode = it
                SettingsStore.save(ctx)
            }

            // 🔊 AUDIO
            SwitchRow("Audio Cues (Default)", audioDefault) {
                audioDefault = it
                SettingsStore.audioEnabled = it
                SettingsStore.save(ctx)
            }

            // 📳 HAPTIC
            SwitchRow("Haptic Feedback (Default)", hapticDefault) {
                hapticDefault = it
                SettingsStore.hapticEnabled = it
                SettingsStore.save(ctx)
            }

            HorizontalDivider()

            Text("Account", style = MaterialTheme.typography.titleMedium)

            ListItem(
                headlineContent = { Text("Account Profile") },
                leadingContent = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.clickable {
                    nav.navigate("profile/$email/$username")
                }
            )

            HorizontalDivider()

            Text("Support", style = MaterialTheme.typography.titleMedium)

            // 📘 HELP
            ListItem(
                headlineContent = { Text("Help & Training Guide") },
                modifier = Modifier.clickable { showHelp = true }
            )

            // 📧 CONTACT SUPPORT — ✅ WORKING BLOCK
            ListItem(
                headlineContent = { Text("Contact Support") },
                supportingContent = { Text("sportsvisiontrainer@gmail.com") },
                modifier = Modifier.clickable {

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:sportsvisiontrainer@gmail.com")
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Sports Vision Trainer Support"
                        )
                    }

                    ctx.startActivity(intent)
                }
            )

            Spacer(Modifier.height(16.dp))

            // 🚪 LOGOUT
            Button(
                onClick = {
                    nav.navigate("login") {
                        popUpTo("start") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }

    // ✅ HELP DIALOG
    if (showHelp) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            confirmButton = {
                TextButton({ showHelp = false }) {
                    Text("Close")
                }
            },
            title = { Text("Sports Vision Trainer Help") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("🎯 Reaction tests measure response time.")
                    Text("👁 Improve visual processing speed.")
                    Text("⚡ Faster taps = better reaction score.")
                    Text("🔊 Audio cues can assist timing.")
                    Text("📳 Haptic gives physical feedback.")
                    Text("🎮 Each game has its own difficulty settings.")
                }
            }
        )
    }
}

@Composable
fun SwitchRow(
    title: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}
