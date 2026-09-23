package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.editor.EditorTool
import com.example.editor.LevelEditorState
import com.example.model.Difficulty
import com.example.model.Level
import com.example.model.ObjectCategory
import com.example.model.ObjectType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    editorState: LevelEditorState,
    onSaveLevel: (Level) -> Unit,
    onPlaytest: (Level) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = editorState.levelName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${editorState.objects.size} objects  •  X: ${String.format("%.1f", editorState.scrollX)}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit", tint = Color.White)
                    }
                },
                actions = {
                    // Undo
                    IconButton(
                        onClick = { editorState.undo() },
                        enabled = editorState.canUndo
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (editorState.canUndo) Color.White else Color.DarkGray
                        )
                    }
                    // Redo
                    IconButton(
                        onClick = { editorState.redo() },
                        enabled = editorState.canRedo
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (editorState.canRedo) Color.White else Color.DarkGray
                        )
                    }
                    // Settings
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF00E5FF))
                    }
                    // Playtest
                    Button(
                        onClick = { onPlaytest(editorState.buildLevel()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("TEST", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // Save
                    Button(
                        onClick = {
                            val lvl = editorState.buildLevel()
                            onSaveLevel(lvl)
                            Toast.makeText(context, "Level '${lvl.name}' saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("SAVE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1117))
            )
        },
        containerColor = Color(0xFF0B0E14)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Interactive Grid Canvas (Middle Area)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(editorState.bgColor))
                    .pointerInput(editorState.activeTool, editorState.selectedType) {
                        detectTapGestures { offset ->
                            val tileSize = size.height / 10f
                            val groundY = size.height - (tileSize * 1.5f)
                            val gridX = editorState.scrollX + (offset.x / tileSize)
                            val gridY = (groundY - offset.y) / tileSize
                            editorState.onGridTapped(gridX, gridY)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val tileSize = size.height / 10f
                            val deltaGrid = -dragAmount.x / tileSize
                            editorState.scrollX = (editorState.scrollX + deltaGrid).coerceAtLeast(0f)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val tileSize = h / 10f
                    val groundScreenY = h - (tileSize * 1.5f)

                    // Draw Ground
                    drawRect(
                        color = Color(editorState.groundColor),
                        topLeft = Offset(0f, groundScreenY),
                        size = Size(w, h - groundScreenY)
                    )
                    drawLine(
                        color = Color(0xFF00E5FF),
                        start = Offset(0f, groundScreenY),
                        end = Offset(w, groundScreenY),
                        strokeWidth = 3f
                    )

                    // Draw Grid lines
                    val scrollOffset = (editorState.scrollX * tileSize) % tileSize
                    val gridColor = Color.White.copy(alpha = 0.08f)
                    var cx = -scrollOffset
                    var gridIndex = (editorState.scrollX - (editorState.scrollX % 1f)).toInt()
                    while (cx < w) {
                        drawLine(
                            color = gridColor,
                            start = Offset(cx, 0f),
                            end = Offset(cx, groundScreenY),
                            strokeWidth = 1f
                        )
                        cx += tileSize
                    }

                    for (yStep in 0..8) {
                        val sy = groundScreenY - (yStep * tileSize)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, sy),
                            end = Offset(w, sy),
                            strokeWidth = 1f
                        )
                    }

                    // Render placed objects
                    for (obj in editorState.objects) {
                        val screenX = (obj.x - editorState.scrollX) * tileSize
                        val screenY = groundScreenY - ((obj.y + obj.type.height) * tileSize)
                        val objW = obj.type.width * tileSize
                        val objH = obj.type.height * tileSize

                        if (screenX + objW >= 0 && screenX <= w) {
                            val color = Color(obj.type.primaryColorHex)
                            drawRect(
                                color = color.copy(alpha = 0.85f),
                                topLeft = Offset(screenX, screenY),
                                size = Size(objW, objH)
                            )
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(screenX, screenY),
                                size = Size(objW, objH),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }

                // Quick Tool Floater (Draw / Erase)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color(0xFF161B22).copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { editorState.activeTool = EditorTool.PLACE },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (editorState.activeTool == EditorTool.PLACE) Color(0xFF00E5FF) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Place",
                            tint = if (editorState.activeTool == EditorTool.PLACE) Color.Black else Color.White
                        )
                    }
                    IconButton(
                        onClick = { editorState.activeTool = EditorTool.ERASE },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (editorState.activeTool == EditorTool.ERASE) Color.Red else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Erase",
                            tint = if (editorState.activeTool == EditorTool.ERASE) Color.White else Color.Gray
                        )
                    }
                }
            }

            // Timeline Scrubber
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIMELINE:",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = editorState.scrollX.coerceIn(0f, 150f),
                    onValueChange = { editorState.scrollX = it },
                    valueRange = 0f..150f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${editorState.scrollX.toInt()}m",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom Palette Tray
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1117))
            ) {
                // Category tabs
                ScrollableTabRow(
                    selectedTabIndex = editorState.selectedCategory.ordinal,
                    containerColor = Color(0xFF161B22),
                    contentColor = Color(0xFF00E5FF),
                    edgePadding = 8.dp
                ) {
                    ObjectCategory.values().forEach { cat ->
                        Tab(
                            selected = editorState.selectedCategory == cat,
                            onClick = {
                                editorState.selectedCategory = cat
                                editorState.selectedType = ObjectType.values().first { it.category == cat }
                            },
                            text = {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (editorState.selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Available Objects in selected category
                val currentCategoryObjects = ObjectType.values().filter { it.category == editorState.selectedCategory }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(currentCategoryObjects) { type ->
                        val isSelected = editorState.selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                editorState.selectedType = type
                                editorState.activeTool = EditorTool.PLACE
                            },
                            label = {
                                Text(
                                    text = type.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(Color(type.primaryColorHex), CircleShape)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(type.primaryColorHex).copy(alpha = 0.25f),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }

    // Level Settings Modal
    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
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
                        .padding(20.dp)
                ) {
                    Text(
                        text = "LEVEL SETTINGS",
                        color = Color(0xFF00E5FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editorState.levelName,
                        onValueChange = { editorState.levelName = it },
                        label = { Text("Level Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editorState.levelDescription,
                        onValueChange = { editorState.levelDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Music selection
                    Text("Soundtrack:", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    val songNames = listOf("Stereo Track", "Cyber Beat", "Dark Demon", "Neon Pulse")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        songNames.forEachIndexed { idx, name ->
                            val isSel = editorState.musicTrack == idx
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSel) Color(0xFF00E5FF) else Color(0xFF21262D),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editorState.musicTrack = idx }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = name.take(7),
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target Difficulty
                    Text("Target Difficulty:", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD, Difficulty.HARDER, Difficulty.INSANE, Difficulty.DEMON).forEach { d ->
                            val isSel = editorState.difficulty == d
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSel) Color(d.colorHex) else Color(0xFF21262D),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        editorState.difficulty = d
                                        editorState.stars = d.defaultStars
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = d.displayName.take(3),
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clear Level Button
                    Button(
                        onClick = {
                            editorState.clearAll()
                            Toast.makeText(context, "All objects cleared", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331414)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CLEAR ALL OBJECTS", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showSettingsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("DONE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
