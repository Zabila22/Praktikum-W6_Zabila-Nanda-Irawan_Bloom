
package com.example.myapplication_zabilanandairawan_2301_pab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileCream = Color(0xFFFFFAF4)
private val ProfilePink = Color(0xFFF5DCE5)
private val ProfileLavender = Color(0xFFE8E0F7)
private val ProfileBrown = Color(0xFF59454E)

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("Bloom Girl") }
    var editing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileCream)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "My Profile 🌷",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileBrown,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            color = ProfilePink
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🌸", fontSize = 52.sp)
            }
        }

        Text(
            text = "Your own little space",
            color = ProfileBrown,
            fontSize = 14.sp
        )

        if (editing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your name") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = { editing = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD985A2)
                )
            ) {
                Text("Save Profile ♡")
            }
        } else {
            Text(
                text = name,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileBrown
            )

            Button(
                onClick = { editing = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD985A2)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Edit Profile ✎")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ProfileLavender
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "A little reminder 💌",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ProfileBrown
                )
                Text(
                    text = "You deserve the same kindness you give to others.",
                    color = ProfileBrown
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ProfilePink
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About Bloom 🌷",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ProfileBrown
                )
                Text(
                    text = "Bloom is your cozy space to check in with your mood, " +
                            "collect positive reminders, and make time for self-care.",
                    color = ProfileBrown
                )
            }
        }
    }
}
