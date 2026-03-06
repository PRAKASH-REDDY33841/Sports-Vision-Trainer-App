package com.example.sports_vision_trainer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import com.example.sports_vision_trainer.screens.*
import com.example.sports_vision_trainer.ui.theme.Sports_vision_trainerTheme

class MainActivity : ComponentActivity() {

    private val notifPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        setContent {

            val ctx = LocalContext.current

            LaunchedEffect(Unit) {
                SettingsStore.load(ctx)
            }

            var darkMode by remember {
                mutableStateOf(SettingsStore.darkMode)
            }

            Sports_vision_trainerTheme(darkTheme = darkMode) {

                val nav = rememberNavController()

                // ✅ FIX 1 — STORE EMAIL & NAME
                var currentEmail by remember { mutableStateOf("") }
                var currentName by remember { mutableStateOf("") }

                NavHost(
                    navController = nav,
                    startDestination = "start"
                ) {

                    // ---------- AUTH ----------

                    composable("start") { GetStartedScreen(nav) }
                    composable("login") { LoginScreen(nav) }
                    composable("register") { RegisterScreen(nav) }
                    composable("forgot") { ForgotPasswordScreen(nav) }

                    composable("otp/{email}") {
                        OtpScreen(nav, it.arguments?.getString("email") ?: "")
                    }

                    composable("reset/{email}") {
                        ResetPasswordScreen(nav, it.arguments?.getString("email") ?: "")
                    }

                    // ---------- HOME ----------

                    composable("home/{email}/{name}") { back ->

                        val email =
                            back.arguments?.getString("email") ?: ""

                        val name =
                            back.arguments?.getString("name") ?: "User"

                        // ✅ FIX 2 — SAVE EMAIL & NAME
                        currentEmail = email
                        currentName = name

                        MainScreen(
                            navController = nav,
                            email = email,
                            username = name
                        ) {

                            HomeScreen(
                                nav = nav,
                                email = email,
                                username = name
                            )
                        }
                    }

                    // ---------- PROFILE ----------

                    composable("profile/{email}/{name}") { back ->
                        ProfileScreen(
                            nav,
                            back.arguments?.getString("email") ?: "",
                            back.arguments?.getString("name") ?: "User"
                        )
                    }

                    composable("settings/{email}/{name}") { back ->
                        SettingsScreen(
                            nav,
                            back.arguments?.getString("email") ?: "",
                            back.arguments?.getString("name") ?: "User",
                            onDarkModeChange = {
                                darkMode = it
                                SettingsStore.darkMode = it
                                SettingsStore.save(ctx)
                            }
                        )
                    }

                    // ---------- SESSION ----------

                    composable("session_start/{origin}") { back ->
                        SessionStartScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    composable("reaction_game/{origin}") { back ->
                        ReactionJumpGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    composable(
                        "reaction_result/{taps}/{avg}/{misses}/{score}/{source}/{origin}"
                    ) { back ->

                        val taps =
                            back.arguments?.getString("taps")?.toIntOrNull() ?: 0

                        val avg =
                            back.arguments?.getString("avg")?.toLongOrNull() ?: 0L

                        val misses =
                            back.arguments?.getString("misses")?.toIntOrNull() ?: 0

                        val score =
                            back.arguments?.getString("score")?.toIntOrNull() ?: 0

                        val source =
                            back.arguments?.getString("source") ?: "jump"

                        val origin =
                            back.arguments?.getString("origin") ?: "home"

                        ReactionResultScreen(
                            nav = nav,
                            taps = taps,
                            avgReaction = avg,
                            misses = misses,
                            score = score,
                            source = source,
                            origin = origin
                        )
                    }

                    // ---------- COLOR TAP ----------

                    composable("color_tap_countdown/{origin}") { back ->
                        ColorTapCountdownScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    composable("color_tap/{origin}") { back ->
                        ColorTapStimulusScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    // ---------- DIRECTION SWIPE ----------

                    composable("direction_swipe_countdown/{origin}") { back ->
                        DirectionSwipeCountdownScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    composable("direction_swipe_game/{origin}") { back ->
                        DirectionSwipeGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "home"
                        )
                    }

                    // ---------- BOTTOM TAB SCREENS ----------

                    composable("exercise") {
                        MainScreen(nav, currentEmail, currentName) {
                            ExerciseScreen(nav)
                        }
                    }

                    composable("sports") {
                        MainScreen(nav, currentEmail, currentName) {
                            SportsScreen(nav)
                        }
                    }

                    composable("stats") {
                        MainScreen(nav, currentEmail, currentName) {
                            StatsScreen(nav)
                        }
                    }

                    // ---------- SPORTS GAMES ----------

                    composable("moving_target_game/{origin}") { back ->
                        MovingTargetTapGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("countdown/{game}/{origin}") { back ->
                        GameCountdownScreen(
                            nav = nav,
                            game = back.arguments?.getString("game") ?: "moving",
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("gonogo_game/{origin}") { back ->
                        GoNoGoFlashGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("peripheral_game/{origin}") { back ->
                        PeripheralDotCatchGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("color_direction_game/{origin}") { back ->
                        ColorDirectionGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("countdown_burst_game/{origin}") { back ->
                        CountdownBurstGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("random_target_game/{origin}") { back ->
                        RandomTargetGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("dual_color_game/{origin}") { back ->
                        DualColorGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("number_reaction_game/{origin}") { back ->
                        NumberReactionGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("arrow_rush_game/{origin}") { back ->
                        ArrowRushGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("distraction_layer/{origin}") { back ->
                        DistractionLayerGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("memory_grid/{origin}") { back ->
                        MemoryGridGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }

                    composable("sound_visual_game/{origin}") { back ->
                        SoundVisualGameScreen(
                            nav = nav,
                            origin = back.arguments?.getString("origin") ?: "sports"
                        )
                    }
                }
            }
        }
    }
}