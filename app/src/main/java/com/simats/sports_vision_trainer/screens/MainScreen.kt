package com.simats.sports_vision_trainer.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainScreen(
    navController: NavController,
    email: String,
    username: String,
    content: @Composable (Modifier) -> Unit
) {

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute?.startsWith("home") == true,
                    onClick = {
                        navController.navigate("home/$email/$username") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = currentRoute == "exercise",
                    onClick = {
                        navController.navigate("exercise") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Star, null) },
                    label = { Text("Exercise") }
                )

                NavigationBarItem(
                    selected = currentRoute == "sports",
                    onClick = {
                        navController.navigate("sports") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.PlayArrow, null) },
                    label = { Text("Sports") }
                )

                NavigationBarItem(
                    selected = currentRoute == "stats",
                    onClick = {
                        navController.navigate("stats") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text("Stats") }
                )
            }
        }
    ) { paddingValues ->

        // ✅ FIX IS HERE
        content(Modifier.padding(paddingValues))
    }
}