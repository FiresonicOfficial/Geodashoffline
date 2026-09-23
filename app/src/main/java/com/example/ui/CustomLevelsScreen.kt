package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.Level

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLevelsScreen(
    customLevels: List<Level>,
    onPlayLevel: (level: Level, isPractice: Boolean) -> Unit,
    onEditLevel: (level: Level) -> Unit,
    onCreateNewLevel: () -> Unit,
    onDeleteLevel: (id: String) -> Unit,
    onRateLevel: (level: Level, score: Float, difficultyVote: Difficulty, isLiked: Boolean) -> Unit,
    onImportLevel: (level: Level) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var levelToRate by remember { mutableStateOf<Level?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importCodeText by remember { mutableStateOf("") }

    val tabs = listOf("All Custom", "Top Rated", "Demons", "My Creations")

    val filteredLevels = when (selectedTab) {
        1 -> customLevels.sortedByDescending { it.userRating }
        2 -> customLevels.filter { it.difficulty == Difficulty.DEMON }
        3 -> customLevels.filter { it.author == "Player" || it.author == "You" }
        else -> customLevels.sortedByDescending { it.createdAt }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CUSTOM LEVELS & RATINGS",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Import Level Code",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1117))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNewLevel,
                containerColor = Color(0xFF00E5FF),
                contentColor = Color.Black
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Level")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("NEW LEVEL", fontWeight = FontWeight.Black)
                }
            }
        },
        containerColor = Color(0xFF0B0E14)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF161B22),
                contentColor = Color(0xFF00E5FF),
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF00E5FF) else Color.Gray
                            )
                        }
                    )
                }
            }

            if (filteredLevels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No custom levels found.",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap '+ NEW LEVEL' below to build your own or import an offline level code!",
                            color = Color.DarkGray,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLevels, key = { it.id }) { level ->
                        CustomLevelCard(
                            level = level,
                            onPlay = { isPractice -> onPlayLevel(level, isPractice) },
                            onRate = { levelToRate = level },
                            onEdit = { onEditLevel(level) },
                            onExport = {
                                val code = Level.exportLevelCode(level)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("GeoDash Level", code)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Level code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = { onDeleteLevel(level.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Rating Dialog
    levelToRate?.let { level ->
        RatingDialog(
            level = level,
            onDismiss = { levelToRate = null },
            onSubmit = { stars, difficultyVote, isLiked ->
                onRateLevel(level, stars, difficultyVote, isLiked)
                levelToRate = null
                Toast.makeText(context, "Rating submitted!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("IMPORT LEVEL CODE", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste an exported level code string:", color = Color.LightGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importCodeText,
                        onValueChange = { importCodeText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Paste base64 code here...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val imported = Level.importLevelCode(importCodeText)
                        if (imported != null) {
                            onImportLevel(imported)
                            showImportDialog = false
                            importCodeText = ""
                            Toast.makeText(context, "Level imported successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Invalid level code format.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("IMPORT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF161B22)
        )
    }
}

@Composable
private fun CustomLevelCard(
    level: Level,
    onPlay: (isPractice: Boolean) -> Unit,
    onRate: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(level.difficulty.colorHex).copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty Badge
                DifficultyBadge(
                    difficulty = level.difficulty,
                    size = 54.dp,
                    showLabel = false,
                    stars = level.stars
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = level.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by ${level.author}  •  ${level.objects.size} objects",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // Rating Stars
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (level.userRatingsCount > 0) String.format("%.1f", level.userRating) else "Unrated",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${level.userRatingsCount})",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Likes",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${level.likesCount}",
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Play Button
                Button(
                    onClick = { onPlay(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PLAY", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action toolbar: Rate, Practice, Edit, Export, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    // Rate button
                    Button(
                        onClick = onRate,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Rate", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RATE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Practice
                    Button(
                        onClick = { onPlay(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("PRACTICE", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onExport, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Export Code", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
