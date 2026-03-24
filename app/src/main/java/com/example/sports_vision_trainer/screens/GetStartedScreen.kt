package com.example.sports_vision_trainer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sports_vision_trainer.R

@Composable
fun GetStartedScreen(nav: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        /* ---------------- TOP TEXT ---------------- */

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "VISION TRAINING\nFOR ATHLETES",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Improve reaction speed and focus.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        /* ---------------- CENTER LOGO ---------------- */

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.vision_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.65f)   // 👈 Bigger & proportional
                    .aspectRatio(1f)
            )
        }

        /* ---------------- BUTTON ---------------- */

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = { nav.navigate("role_selection") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B4F25)
                )
            ) {
                Text(
                    text = "GET STARTED",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
