package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.Level

@Composable
fun StatsScreen(
    levels: List<Level>,
    onBack: () -> Unit
) {
    val totalStars = levels.filter { it.completed }.sumOf { it.stars }
    val totalCoins = levels.sumOf { it.coinsCollected }
    val demonsBeaten = levels.count { it.completed && it.difficulty == Difficulty.DEMON }
    val totalAttempts = levels.sumOf { it.attempts }
    val totalJumps = levels.sumOf { it.jumps }
    val totalScore = levels.sumOf { it.highScore }
    val levelsCompleted = levels.count { it.completed }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF090D16),
                        Color(0xFF0B1426),
                        Color(0xFF140D26)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F2E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "PLAYER STATS & HIGH SCORES",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "TOTAL SCORE: $totalScore PTS",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2-COLUMN LANDSCAPE LAYOUT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LEFT PANE: Profile & Summary
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121824)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFFFFE600), RoundedCornerShape(12.dp))
                                    .border(2.5.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF0B0E1B), RoundedCornerShape(2.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF0B0E1B), RoundedCornerShape(2.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "DASH MASTER",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (demonsBeaten > 0) "Demon Slayer" else if (totalStars >= 10) "Pro Dasher" else "Novice Jumper",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$levelsCompleted / ${levels.size} Cleared",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Stat metrics
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatRowItem(Icons.Default.Star, "Stars Won", "$totalStars", Color(0xFFFFD700))
                            StatRowItem(Icons.Default.EmojiEvents, "Secret Coins", "$totalCoins", Color(0xFFFF9100))
                            StatRowItem(Icons.Default.Favorite, "Demons Beaten", "$demonsBeaten", Color(0xFFE040FB))
                            StatRowItem(Icons.Default.Loop, "Total Attempts", "$totalAttempts", Color(0xFF00E5FF))
                            StatRowItem(Icons.Default.FlightTakeoff, "Total Jumps", "$totalJumps", Color(0xFF00E676))
                        }
                    }
                }

                // RIGHT PANE: Level High Scores Leaderboard Table
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121824)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF223046)),
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "CAMPAIGN HIGH SCORES",
                            color = Color(0xFF00E5FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(levels) { level ->
                                HighScoreItemCard(level)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRowItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF182233), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun HighScoreItemCard(level: Level) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF172030), RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (level.completed) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF223046),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (level.completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                } else if (!level.isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = level.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (level.bestPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(4.dp),
                color = if (level.completed) Color(0xFF00E676) else Color(0xFF00E5FF),
                trackColor = Color(0xFF223046)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${level.highScore} pts",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
            Text(
                text = "Best: ${level.bestPercentage}%",
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }
    }
}
