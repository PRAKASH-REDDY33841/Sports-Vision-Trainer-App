package com.simats.sports_vision_trainer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RoleSelectionScreen(nav: NavController) {

    val backgroundColor = Color(0xFFFDF9F1)
    val darkBrownText = Color(0xFF2D1E16)
    val lightCreamCard = Color(0xFFFDF9F1)
    val cardBorder = Color(0xFFEADFCD)
    val darkBrownCard = Color(0xFF2D1E16)
    val grayText = Color(0xFF757575)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "VISION TRAINING\n", // using \n to closely match layout if needed, actually maybe just plain string
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkBrownText,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // DOCTOR CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nav.navigate("doctor_login") }
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightCreamCard),
            border = BorderStroke(1.dp, cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Doctor Icon",
                        tint = darkBrownText,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "DOCTOR",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = darkBrownText,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Access diagnostic tools, manage patient records, and track rehabilitation progress.",
                    fontSize = 13.sp,
                    color = grayText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ATHLETE CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nav.navigate("login") }
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = darkBrownCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Athlete Icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ATHLETE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Train with high-intensity ocular drills to improve reaction speed, focus, and athletic performance.",
                    fontSize = 13.sp,
                    color = Color(0xFFD6CFC9), // light gray/brown for dark background
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
