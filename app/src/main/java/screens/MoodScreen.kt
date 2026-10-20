
package com.example.myapplication_zabilanandairawan_2301_pab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MoodCream = Color(0xFFFFFAF4)
private val MoodPink = Color(0xFFF5DCE5)
private val MoodLavender = Color(0xFFE8E0F7)
private val MoodBrown = Color(0xFF59454E)

@Composable
fun MoodScreen() {
    var selectedMood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var savedMessage by remember { mutableStateOf(false) }

    val moods = listOf(
        "😊" to "Happy",
        "😌" to "Calm",
        "🥰" to "Loved",
        "😴" to "Tired",
        "😢" to "Sad",
        "😣" to "Stressed"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MoodCream)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "My Mood 🌷",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MoodBrown
        )

        Text(
            text = "It's okay to feel what you feel.",
            fontSize = 16.sp,
            color = MoodBrown
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MoodPink
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "How are you feeling today?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoodBrown
                )

                moods.chunked(3).forEach { rowMoods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMoods.forEach { mood ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedMood = mood.second
                                        savedMessage = false
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        if (selectedMood == mood.second)
                                            MoodLavender
                                        else
                                            MoodCream
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp),
                                    horizontalAlignment =
                                        androidx.compose.ui.Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = mood.first,
                                        fontSize = 27.sp
                                    )
                                    Text(
                                        text = mood.second,
                                        fontSize = 12.sp,
                                        color = MoodBrown
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Write your thoughts 💌",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MoodBrown
        )

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
                savedMessage = false
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("What's on your mind today?")
            },
            minLines = 4,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MoodPink,
                unfocusedBorderColor = MoodLavender,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Button(
            onClick = {
                savedMessage = selectedMood.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD985A2)
            )
        ) {
            Text(
                text = "Save My Mood ♡",
                fontSize = 16.sp
            )
        }

        if (savedMessage) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MoodLavender
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Your mood today: $selectedMood 🌸\n" +
                            "Your feelings matter. Thank you for checking in with yourself!",
                    modifier = Modifier.padding(16.dp),
                    color = MoodBrown
                )
            }
        } else if (selectedMood.isEmpty()) {
            Text(
                text = "Choose a mood first, then save your check-in ♡",
                color = MoodBrown,
                fontSize = 13.sp
            )
        }
    }
}
