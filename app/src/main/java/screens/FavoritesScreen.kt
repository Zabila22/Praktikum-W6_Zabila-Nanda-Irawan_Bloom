
package com.example.myapplication_zabilanandairawan_2301_pab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FavCream = Color(0xFFFFFAF4)
private val FavPink = Color(0xFFF5DCE5)
private val FavLavender = Color(0xFFE8E0F7)
private val FavBrown = Color(0xFF59454E)

@Composable
fun FavoritesScreen() {
    var likedItems by remember {
        mutableStateOf(setOf(0, 1))
    }

    val items = listOf(
        Triple("🌷", "Be gentle with yourself", "Positive affirmation"),
        Triple("🧘", "Take a mindful break", "Self-care activity"),
        Triple("🌸", "Celebrate small wins", "Daily reminder"),
        Triple("☕", "Enjoy a quiet moment", "Self-care activity")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FavCream)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "My Favorites ♡",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FavBrown
        )

        Text(
            text = "Little things that make you feel better.",
            fontSize = 14.sp,
            color = FavBrown
        )

        items.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (index % 2 == 0) {
                        FavPink
                    } else {
                        FavLavender
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.first,
                            fontSize = 28.sp
                        )
                        Text(
                            text = item.second,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FavBrown
                        )
                        Text(
                            text = item.third,
                            fontSize = 12.sp,
                            color = FavBrown
                        )
                    }

                    TextButton(
                        onClick = {
                            likedItems = if (index in likedItems) {
                                likedItems - index
                            } else {
                                likedItems + index
                            }
                        }
                    ) {
                        Text(
                            text = if (index in likedItems) "♥" else "♡",
                            fontSize = 26.sp,
                            color = Color(0xFFD66F91)
                        )
                    }
                }
            }
        }

        Text(
            text = "${likedItems.size} items in your favorites 🌸",
            fontSize = 14.sp,
            color = FavBrown
        )
    }
}
