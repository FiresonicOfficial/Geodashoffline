package com.example.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.Level

@Composable
fun LevelSelectScreen(
    levels: List<Level>,
    onPlayLevel: (level: Level, isPractice: Boolean) -> Unit,
    onBack: () -> Unit
) {
    if (levels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0E14)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading levels...", color = Color.White)
        }
        return
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val currentLevel = levels[selectedIndex.coerceIn(0, levels.size - 1)]
    val totalCampaignStars = levels.filter { it.completed }.sumOf { it.stars }
    val totalCoins = levels.sumOf { it.coinsCollected }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF090D16),
                        Color(0xFF0D1629),
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
            // TOP BAR (Compact Landscape)
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
                        text = "MAIN CAMPAIGN",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Overall progress badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1B2333), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalCampaignStars Stars",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1B2333), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFFF9100), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Coins",
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalCoins Coins",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MAIN CONTENT (Landscape 2 Columns)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT PANE: Level Carousel & Difficulty Hero
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121824)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(currentLevel.difficulty.colorHex)),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Carousel switcher row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (selectedIndex > 0) selectedIndex--
                                    else selectedIndex = levels.size - 1
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1B2436), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Level",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "LEVEL ${selectedIndex + 1} OF ${levels.size}",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            IconButton(
                                onClick = {
                                    if (selectedIndex < levels.size - 1) selectedIndex++
                                    else selectedIndex = 0
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1B2436), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Level",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Difficulty Hero Badge
                        AnimatedContent(
                            targetState = currentLevel,
                            label = "badge_anim"
                        ) { level ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                DifficultyBadge(
                                    difficulty = level.difficulty,
                                    modifier = Modifier.size(72.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = level.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "${level.difficulty.displayName.uppercase()} • ${level.stars} STARS",
                                    color = Color(level.difficulty.colorHex),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Status pill: Unlocked or Locked
                        if (currentLevel.isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0x2200E676), RoundedCornerShape(20.dp))
                                    .border(1.dp, Color(0xFF00E676), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (currentLevel.completed) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (currentLevel.completed) "STAGE COMPLETED" else "UNLOCKED & READY",
                                        color = Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(Color(0x22FF5252), RoundedCornerShape(20.dp))
                                    .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LOCKED STAGE",
                                        color = Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // RIGHT PANE: Level Progress, High Score & Action Buttons
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121824)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF223046)),
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // High Score & Best Progress
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HIGH SCORE",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${currentLevel.highScore} PTS",
                                    color = Color(0xFFFFD700),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Best Distance:",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${currentLevel.bestPercentage}%",
                                    color = if (currentLevel.completed) Color(0xFF00E676) else Color(0xFF00E5FF),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { (currentLevel.bestPercentage / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = if (currentLevel.completed) Color(0xFF00E676) else Color(0xFF00E5FF),
                                trackColor = Color(0xFF223046)
                            )
                        }

                        // Stats row (Coins, Attempts, Jumps)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Secret Coins
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Coins: ",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                                for (coinIdx in 1..3) {
                                    val collected = coinIdx <= currentLevel.coinsCollected
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(16.dp)
                                            .background(
                                                if (collected) Color(0xFFFFD700) else Color(0xFF223046),
                                                CircleShape
                                            )
                                            .border(
                                                1.dp,
                                                if (collected) Color(0xFFFF9100) else Color.DarkGray,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🪙",
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Attempts: ${currentLevel.attempts}",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            Text(
                                text = "Jumps: ${currentLevel.jumps}",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        // Action Buttons OR Locked Explainer
                        if (currentLevel.isUnlocked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onPlayLevel(currentLevel, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color(0xFF092B15),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "NORMAL",
                                        color = Color(0xFF092B15),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onPlayLevel(currentLevel, true) },
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                ) {
                                    Text(
                                        text = "PRACTICE",
                                        color = Color(0xFF00E5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            // Locked card with unlock requirement
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E1418), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "HOW TO UNLOCK:",
                                            color = Color(0xFFFF5252),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentLevel.unlockRequirement.ifEmpty { "Beat previous levels to unlock" },
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTTOM LEVEL DOT INDICATORS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                levels.forEachIndexed { idx, lvl ->
                    val isSelected = idx == selectedIndex
                    val dotColor = when {
                        isSelected -> Color(0xFF00E5FF)
                        lvl.completed -> Color(0xFF00E676)
                        lvl.isUnlocked -> Color.White
                        else -> Color.DarkGray
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 10.dp else 7.dp)
                            .background(dotColor, CircleShape)
                    )
                }
            }
        }
    }
}
