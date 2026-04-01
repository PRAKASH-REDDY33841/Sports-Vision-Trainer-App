package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DrillGame(
    val name: String,
    val emoji: String,
    val desc: String,
    val colors: List<Color>,
    val route: String? = null
)

@Composable
fun SportsScreen(nav: NavController) {

    val games = listOf(
        DrillGame(
            "Moving Target Tap","🎯","Track movement speed",
            listOf(Color(0xFF667eea), Color(0xFF764ba2)),
            route = "countdown/moving/sports"
        ),
        DrillGame(
            "Go / No-Go Flash","⚡","Impulse control",
            listOf(Color(0xFFf7971e), Color(0xFFffd200)),
            route = "countdown/gonogo/sports"
        ),
        DrillGame(
            "Peripheral Dot Catch","👁️","Spatial awareness",
            listOf(Color(0xFF56ab2f), Color(0xFFa8e063)),
            route = "countdown/peripheral/sports"
        ),
        DrillGame(
            "Color + Direction","🎨","Decision reaction",
            listOf(Color(0xFFff6a00), Color(0xFFee0979)),
            route = "countdown/colordirection/sports"
        ),
        DrillGame(
            "Countdown Burst","⏱️","Start reaction",
            listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
            route = "countdown/burst/sports"
        ),
        DrillGame(
            "Random Target","🔵","Tap accuracy",
            listOf(Color(0xFFcc2b5e), Color(0xFF753a88)),
            route = "countdown/random/sports"
        ),
        DrillGame(
            "Dual Color Tap","🟢","Rule switching",
            listOf(Color(0xFFee9ca7), Color(0xFFffdde1)),
            route = "countdown/dualcolor/sports"
        ),
        DrillGame(
            "Number Reaction","🔢","Order speed",
            listOf(Color(0xFF42275a), Color(0xFF734b6d)),
            route = "countdown/number/sports"
        ),
        DrillGame(
            "Arrow Rush","➡️","Directional reflex",
            listOf(Color(0xFFbdc3c7), Color(0xFF2c3e50)),
            route = "countdown/arrowrush/sports"
        ),
        DrillGame(
            "Distraction Layer","🧠","Focus control",
            listOf(Color(0xFFde6262), Color(0xFFffb88c)),
            route = "countdown/distraction/sports"
        ),
        DrillGame(
            "Memory Grid","🧩","Working memory",
            listOf(Color(0xFF06beb6), Color(0xFF48b1bf)),
            route = "countdown/memory/sports"
        ),
        DrillGame(
            "Sound + Visual","🔊","Multi-signal reaction",
            listOf(Color(0xFF614385), Color(0xFF516395)),
            route = "countdown/soundvisual/sports"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // ✅ CORRECTED BACK ARROW

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Vision Training",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Sharpen your reaction & focus",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.width(30.dp))
        }

        Spacer(Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(games) { game ->
                GameEmojiCard(game) {
                    game.route?.let { nav.navigate(it) }
                }
            }
        }
    }
}

@Composable
fun GameEmojiCard(game: DrillGame, onClick: () -> Unit) {

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable { onClick() }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(game.colors))
                .padding(14.dp)
        ) {

            Text(
                game.emoji,
                fontSize = 40.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            Column(Modifier.align(Alignment.BottomStart)) {

                Text(
                    game.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    game.desc,
                    color = Color.White.copy(.9f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
