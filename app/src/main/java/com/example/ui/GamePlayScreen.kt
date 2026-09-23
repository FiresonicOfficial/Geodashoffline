package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.GameAudioEngine
import com.example.game.GameEngine
import com.example.game.GameRenderer
import com.example.model.Difficulty
import com.example.model.Level

@Composable
fun GamePlayScreen(
    level: Level,
    isPracticeMode: Boolean = false,
    onFinish: (percentage: Int, completed: Boolean, attempts: Int, jumps: Int, coins: Int) -> Unit,
    onRateLevel: (score: Float, difficultyVote: Difficulty, isLiked: Boolean) -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var beatPulseVal by remember { mutableFloatStateOf(0f) }
    var currentPercentage by remember { mutableIntStateOf(0) }
    var attemptDisplay by remember { mutableIntStateOf(1) }
    var frameTick by remember { mutableLongStateOf(0L) }

    // Start audio track corresponding to level
    DisposableEffect(level) {
        GameAudioEngine.currentTrackId = level.musicTrack
        GameAudioEngine.onBeatPulse = { pulse ->
            beatPulseVal = pulse
        }
        onDispose {
            GameAudioEngine.onBeatPulse = null
        }
    }

    val engine = remember(level, isPracticeMode) {
        GameEngine(
            level = level,
            isPracticeMode = isPracticeMode,
            onLevelCompleted = { attempts, jumps, coins ->
                showVictoryDialog = true
                onFinish(100, true, attempts, jumps, coins)
            },
            onDeath = { _ ->
                // Death event
            }
        )
    }

    // High performance game loop (runs at native display refresh rate: 60/90/120 Hz)
    LaunchedEffect(isPaused, showVictoryDialog) {
        var lastFrameNanos = 0L
        while (!isPaused && !showVictoryDialog) {
            withFrameNanos { frameNanos ->
                if (lastFrameNanos != 0L) {
                    val dt = ((frameNanos - lastFrameNanos) / 1_000_000_000f).coerceIn(0.001f, 0.04f)
                    engine.update(dt)

                    // Only trigger HUD state writes when values actually change
                    if (engine.percentage != currentPercentage) {
                        currentPercentage = engine.percentage
                    }
                    if (engine.attemptsCount != attemptDisplay) {
                        attemptDisplay = engine.attemptsCount
                    }
                    if (beatPulseVal > 0f) {
                        beatPulseVal = (beatPulseVal - dt * 2.5f).coerceAtLeast(0f)
                    }

                    // Invalidate Canvas draw phase directly without full Composable recomposition
                    frameTick = frameNanos
                }
                lastFrameNanos = frameNanos
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(isPaused, showVictoryDialog) {
                if (isPaused || showVictoryDialog) return@pointerInput
                awaitEachGesture {
                    awaitFirstDown()
                    engine.onPointerDown()
                    waitForUpOrCancellation()
                    engine.onPointerUp()
                }
            }
    ) {
        // Main Game Canvas (Only the draw lambda invalidates on frameTick)
        Canvas(modifier = Modifier.fillMaxSize().testTag("game_canvas")) {
            val tick = frameTick
            val cameraX = engine.playerX - 3.5f
            GameRenderer.renderGame(
                scope = this,
                engine = engine,
                beatPulse = beatPulseVal,
                cameraX = cameraX
            )
        }

        // Top HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause button
                IconButton(
                    onClick = { isPaused = true },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                }

                // Level Name & Practice Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = level.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (isPracticeMode) {
                        Text(
                            text = "PRACTICE MODE",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Attempt display
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ATTEMPT $attemptDisplay",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { currentPercentage / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF00E5FF),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$currentPercentage%",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Practice Mode Checkpoint Buttons (Bottom Right)
        if (isPracticeMode) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Remove checkpoint button
                IconButton(
                    onClick = { engine.removeLastCheckpoint() },
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF21262D).copy(alpha = 0.85f), CircleShape)
                        .border(2.dp, Color.Red, CircleShape)
                        .testTag("undo_checkpoint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo Checkpoint",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Place checkpoint button (green diamond)
                IconButton(
                    onClick = { engine.addCheckpoint() },
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.85f), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .testTag("add_checkpoint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Place Checkpoint",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Pause Menu Dialog
        if (isPaused) {
            Dialog(onDismissRequest = { isPaused = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GAME PAUSED",
                            color = Color(0xFF00E5FF),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Current Progress: $currentPercentage%",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Resume
                        Button(
                            onClick = { isPaused = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("RESUME", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Restart
                        Button(
                            onClick = {
                                engine.resetPlayer(softReset = false)
                                isPaused = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("RESTART", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Exit to Menu
                        Button(
                            onClick = {
                                onFinish(currentPercentage, false, engine.attemptsCount, engine.jumpsCount, engine.collectedCoinIds.size)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331414)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXIT TO MENU", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Victory Dialog (100% Reached!)
        if (showVictoryDialog) {
            Dialog(onDismissRequest = { /* Continue */ }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFFD700)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LEVEL COMPLETE!",
                            color = Color(0xFFFFD700),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "100%",
                            color = Color(0xFF00E676),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stars and Coins Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+${level.stars}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                Text("Stars Earned", color = Color.Gray, fontSize = 11.sp)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${engine.collectedCoinIds.size}/3", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                Text("Coins Found", color = Color.Gray, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Attempts: ${engine.attemptsCount}  •  Jumps: ${engine.jumpsCount}",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Rate Level button
                        Button(
                            onClick = { showRateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RATE THIS LEVEL", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Continue Button
                        Button(
                            onClick = {
                                showVictoryDialog = false
                                onFinish(100, true, engine.attemptsCount, engine.jumpsCount, engine.collectedCoinIds.size)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("CONTINUE", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Rate Dialog from Victory
        if (showRateDialog) {
            RatingDialog(
                level = level,
                onDismiss = { showRateDialog = false },
                onSubmit = { stars, difficultyVote, isLiked ->
                    onRateLevel(stars, difficultyVote, isLiked)
                    showRateDialog = false
                }
            )
        }
    }
}
