package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioEngine
import com.example.data.PlayerProgressEntity

@Composable
fun MainMenuScreen(
    playerProgress: PlayerProgressEntity?,
    onResumeCampaign: () -> Unit,
    onOpenMainLevels: () -> Unit,
    onOpenEditor: () -> Unit,
    onOpenCustomLevels: () -> Unit,
    onOpenStats: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(GameAudioEngine.isMuted) }
    var beatPulse by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        GameAudioEngine.start()
        GameAudioEngine.currentTrackId = 0
        GameAudioEngine.onBeatPulse = { pulse ->
            beatPulse = pulse
        }
        onDispose {
            GameAudioEngine.onBeatPulse = null
        }
    }

    // Infinite bouncing animation for cube hero
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 760, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val resumeLevelName = playerProgress?.resumeLevelName ?: "Stereo Madness"
    val resumePercentage = playerProgress?.resumePercentage ?: 0
    val resumeScore = playerProgress?.resumeHighScore ?: 0L
    val unlockedCount = playerProgress?.getUnlockedLevelIds()?.size ?: 1
    val totalStars = playerProgress?.totalStars ?: 0
    val totalCoins = playerProgress?.totalCoins ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF090D16),
                        Color(0xFF0B1426),
                        Color(0xFF140D26)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1200f, 800f)
                )
            )
    ) {
        // Subtle cyber grid in the background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridStep = 45f
            val gridColor = Color(0x1500E5FF)
            var curX = 0f
            while (curX < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(curX, 0f),
                    end = Offset(curX, size.height),
                    strokeWidth = 1f
                )
                curX += gridStep
            }
            var curY = 0f
            while (curY < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, curY),
                    end = Offset(size.width, curY),
                    strokeWidth = 1f
                )
                curY += gridStep
            }
        }

        // Sideways / Landscape 2-Column Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT COLUMN: Title + Animated Mascot + RESUME CAMPAIGN Card
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Titles
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GEODASH",
                        color = Color(0xFF00E5FF),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "OFFLINE RHYTHM RUNNER",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Mascot animation: Glowing bouncing cube
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f + bounceY)
                        val cubeSize = 48f + (beatPulse * 8f)

                        // Outer neon glow
                        drawCircle(
                            color = Color(0x3300E5FF),
                            radius = cubeSize * 1.1f,
                            center = center
                        )

                        rotate(spinAngle, pivot = center) {
                            // Cube body
                            drawRect(
                                color = Color(0xFFFFE600),
                                topLeft = Offset(center.x - cubeSize / 2f, center.y - cubeSize / 2f),
                                size = Size(cubeSize, cubeSize)
                            )
                            // Cyan border
                            drawRect(
                                color = Color(0xFF00E5FF),
                                topLeft = Offset(center.x - cubeSize / 2f, center.y - cubeSize / 2f),
                                size = Size(cubeSize, cubeSize),
                                style = Stroke(width = 4f)
                            )
                            // Eyes
                            val eyeOffset = cubeSize * 0.18f
                            val eyeSize = cubeSize * 0.2f
                            drawRect(
                                color = Color(0xFF0B0E1B),
                                topLeft = Offset(center.x - eyeOffset - eyeSize / 2f, center.y - eyeSize / 2f),
                                size = Size(eyeSize, eyeSize)
                            )
                            drawRect(
                                color = Color(0xFF0B0E1B),
                                topLeft = Offset(center.x + eyeOffset - eyeSize / 2f, center.y - eyeSize / 2f),
                                size = Size(eyeSize, eyeSize)
                            )
                        }
                    }
                }

                // RESUME CAMPAIGN HERO CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RESUME CAMPAIGN",
                                color = Color(0xFF00E5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$unlockedCount/6 Unlocked • $totalStars ⭐",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = resumeLevelName,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Best: $resumePercentage% | ${resumeScore}pts",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { (resumePercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = Color(0xFF00E5FF),
                            trackColor = Color(0xFF223046)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onResumeCampaign,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = Color(0xFF092B15),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RESUME LEVEL",
                                color = Color(0xFF092B15),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // RIGHT COLUMN: Top Controls Bar + 2x2 Menu Grid
            Column(
                modifier = Modifier
                    .weight(1.05f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top control bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF161F2E), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2E3E56), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OFFLINE ACTIVE",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            GameAudioEngine.isMuted = isMuted
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F2E), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Audio",
                            tint = if (isMuted) Color.Gray else Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F2E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 2x2 Action Tiles Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Main Campaign
                        MenuTile(
                            modifier = Modifier.weight(1f),
                            title = "CAMPAIGN",
                            subtitle = "6 Official Stages",
                            icon = Icons.Default.PlayArrow,
                            accentColor = Color(0xFFFF9100),
                            gradientColors = listOf(Color(0xFF33200A), Color(0xFF1F1408)),
                            onClick = onOpenMainLevels
                        )

                        // Level Editor
                        MenuTile(
                            modifier = Modifier.weight(1f),
                            title = "EDITOR",
                            subtitle = "Build & Share",
                            icon = Icons.Default.Build,
                            accentColor = Color(0xFF00E5FF),
                            gradientColors = listOf(Color(0xFF0A2933), Color(0xFF071B22)),
                            onClick = onOpenEditor
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Rated & Community
                        MenuTile(
                            modifier = Modifier.weight(1f),
                            title = "CUSTOM & RATED",
                            subtitle = "Stars & Community",
                            icon = Icons.Default.Star,
                            accentColor = Color(0xFFE040FB),
                            gradientColors = listOf(Color(0xFF2E0A33), Color(0xFF1D0621)),
                            onClick = onOpenCustomLevels
                        )

                        // Stats & High Scores
                        MenuTile(
                            modifier = Modifier.weight(1f),
                            title = "HIGH SCORES",
                            subtitle = "Profile & Records",
                            icon = Icons.Default.EmojiEvents,
                            accentColor = Color(0xFFFFD700),
                            gradientColors = listOf(Color(0xFF302B08), Color(0xFF1F1C05)),
                            onClick = onOpenStats
                        )
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(onDismiss = { showSettings = false })
    }
}

@Composable
private fun MenuTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = gradientColors[0]),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
        modifier = modifier
            .height(84.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = subtitle,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape)
                        .border(1.5.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
