
package com.example.myapplication_zabilanandairawan_2301_pab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PinkPastel = Color(0xFFF5DCE5)
private val Cream = Color(0xFFFFFAF4)
private val Lavender = Color(0xFFE8E0F7)
private val DarkBrown = Color(0xFF59454E)

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Bloom 🌷",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Text(
            text = "Hello, beautiful! ♡",
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBrown
        )

        Text(
            text = "Take a little moment for yourself today.",
            color = DarkBrown,
            fontSize = 14.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = PinkPastel
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("How are you feeling today? 💗",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    "Your feelings matter. Take a breath and check in with yourself.",
                    color = DarkBrown
                )
            }
        }

        Text(
            text = "Your little habits ✨",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Lavender
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("♡ Drink enough water", color = DarkBrown)
                Text("♡ Take a short break", color = DarkBrown)
                Text("♡ Write down your thoughts", color = DarkBrown)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Remember: you are growing at your own pace 🌸",
            color = DarkBrown,
            fontSize = 14.sp
        )
    }
}
