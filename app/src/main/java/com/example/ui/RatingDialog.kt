package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Difficulty
import com.example.model.Level

@Composable
fun RatingDialog(
    level: Level,
    onDismiss: () -> Unit,
    onSubmit: (stars: Float, difficultyVote: Difficulty, isLiked: Boolean) -> Unit
) {
    var selectedStars by remember { mutableFloatStateOf(if (level.userRating > 0) level.userRating else 5f) }
    var selectedDifficulty by remember { mutableStateOf(level.difficulty) }
    var isLiked by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RATE LEVEL",
                    color = Color(0xFF00E5FF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = level.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating row (1 to 5)
                Text(
                    text = "Quality Score: ${selectedStars.toInt()}/5 Stars",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { selectedStars = i.toFloat() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = if (i <= selectedStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (i <= selectedStars) Color(0xFFFFD700) else Color.DarkGray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty vote selector
                Text(
                    text = "Suggest Difficulty:",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Difficulty.EASY,
                        Difficulty.NORMAL,
                        Difficulty.HARD,
                        Difficulty.HARDER,
                        Difficulty.INSANE,
                        Difficulty.DEMON
                    ).forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedDifficulty = diff }
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        color = if (isSelected) Color(diff.colorHex) else Color(0xFF21262D),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Gray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = diff.displayName.take(1),
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${diff.defaultStars}★",
                                color = if (isSelected) Color(diff.colorHex) else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Like / Favorite button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF21262D), RoundedCornerShape(12.dp))
                        .clickable { isLiked = !isLiked }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color(0xFFFF1744) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLiked) "Liked!" else "Give a Like (+1)",
                            color = if (isLiked) Color(0xFFFF1744) else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${level.likesCount + if (isLiked) 1 else 0} Likes",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSubmit(selectedStars, selectedDifficulty, isLiked)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text(
                            text = "SUBMIT RATING",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
