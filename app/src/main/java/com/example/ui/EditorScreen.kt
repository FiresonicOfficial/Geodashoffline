package com.example.ui

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.LastPage
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.editor.EditorTool
import com.example.editor.LevelEditorState
import com.example.game.GameRenderer
import com.example.model.Difficulty
import com.example.model.GameObject
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

    // Live continuous saw animation rotation
    val infiniteTransition = rememberInfiniteTransition(label = "saw_anim")
    val editorSawAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "saw_angle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = editorState.levelName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(editorState.difficulty.colorHex).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = editorState.difficulty.displayName.uppercase(),
                                    color = Color(editorState.difficulty.colorHex),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = "${editorState.objects.size} objects • X: ${String.format("%.1f", editorState.scrollX)}m • Zoom: ${(editorState.zoomScale * 100).toInt()}%",
                            color = Color(0xFF00E5FF),
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
                            Toast.makeText(context, "Saved '${lvl.name}' (${lvl.objects.size} objects)", Toast.LENGTH_SHORT).show()
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
                    .pointerInput(editorState.activeTool, editorState.selectedType, editorState.zoomScale) {
                        detectTapGestures { offset ->
                            val baseTileSize = size.height / 10f
                            val tileSize = baseTileSize * editorState.zoomScale
                            val groundY = size.height - (baseTileSize * 1.6f)
                            val gridX = editorState.scrollX + (offset.x / tileSize)
                            val gridY = (groundY - offset.y) / tileSize
                            editorState.onGridTapped(gridX, gridY)
                        }
                    }
                    .pointerInput(editorState.zoomScale) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val baseTileSize = size.height / 10f
                            val tileSize = baseTileSize * editorState.zoomScale
                            val deltaGrid = -dragAmount.x / tileSize
                            editorState.scrollX = (editorState.scrollX + deltaGrid).coerceAtLeast(0f)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val baseTileSize = h / 10f
                    val tileSize = baseTileSize * editorState.zoomScale
                    val groundScreenY = h - (baseTileSize * 1.6f)

                    // Draw Background subtle horizon glow
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(editorState.groundColor).copy(alpha = 0.25f)),
                            startY = groundScreenY - tileSize * 4f,
                            endY = groundScreenY
                        ),
                        topLeft = Offset(0f, groundScreenY - tileSize * 4f),
                        size = Size(w, tileSize * 4f)
                    )

                    // Draw Ground
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(editorState.groundColor), Color.Black),
                            startY = groundScreenY,
                            endY = h
                        ),
                        topLeft = Offset(0f, groundScreenY),
                        size = Size(w, h - groundScreenY)
                    )
                    drawLine(
                        color = Color(0xFF00E5FF),
                        start = Offset(0f, groundScreenY),
                        end = Offset(w, groundScreenY),
                        strokeWidth = 3.5f
                    )

                    // Draw Grid lines with Beat / Mile markers
                    val scrollOffset = (editorState.scrollX * tileSize) % tileSize
                    val gridColor = Color.White.copy(alpha = 0.09f)
                    val beatGridColor = Color(0xFF00E5FF).copy(alpha = 0.22f)

                    var cx = -scrollOffset
                    var curGridUnit = (editorState.scrollX - (editorState.scrollX % 1f)).toInt()
                    while (cx < w) {
                        val isBeat = (curGridUnit % 4) == 0
                        val isMajorMile = (curGridUnit % 10) == 0

                        drawLine(
                            color = if (isMajorMile) Color(0xFFFFD600).copy(alpha = 0.35f) else if (isBeat) beatGridColor else gridColor,
                            start = Offset(cx, 0f),
                            end = Offset(cx, groundScreenY),
                            strokeWidth = if (isMajorMile) 2f else if (isBeat) 1.5f else 1f
                        )
                        cx += tileSize
                        curGridUnit++
                    }

                    // Horizontal Grid lines
                    for (yStep in 0..10) {
                        val sy = groundScreenY - (yStep * tileSize)
                        if (sy >= 0) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, sy),
                                end = Offset(w, sy),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // Render placed objects with authentic GameRenderer engine
                    val minX = editorState.scrollX - 2f
                    val maxX = editorState.scrollX + (w / tileSize) + 2f

                    for (obj in editorState.objects) {
                        if (obj.x + obj.type.width < minX || obj.x > maxX) continue
                        GameRenderer.drawGameObject(
                            scope = this,
                            obj = obj,
                            cameraX = editorState.scrollX,
                            groundScreenY = groundScreenY,
                            tileSize = tileSize,
                            sawAngle = editorSawAngle,
                            isEditor = true
                        )
                    }
                }

                // Quick Tool HUD: Place / Erase / Zoom / Nav
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color(0xFF161B22).copy(alpha = 0.92f), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFF30363D)), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tool: Place
                    IconButton(
                        onClick = { editorState.activeTool = EditorTool.PLACE },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (editorState.activeTool == EditorTool.PLACE) Color(0xFF00E5FF) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Place Mode",
                            tint = if (editorState.activeTool == EditorTool.PLACE) Color.Black else Color.White
                        )
                    }

                    // Tool: Erase
                    IconButton(
                        onClick = { editorState.activeTool = EditorTool.ERASE },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (editorState.activeTool == EditorTool.ERASE) Color(0xFFFF1744) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Erase Mode",
                            tint = if (editorState.activeTool == EditorTool.ERASE) Color.White else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Jump to Start
                    IconButton(
                        onClick = { editorState.jumpToStart() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.FirstPage, contentDescription = "Jump to Start", tint = Color.LightGray)
                    }

                    // Jump to End
                    IconButton(
                        onClick = { editorState.jumpToEnd() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.LastPage, contentDescription = "Jump to End", tint = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Zoom Out
                    IconButton(
                        onClick = { editorState.zoomOut() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.LightGray)
                    }

                    // Zoom Label & Reset
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF21262D), RoundedCornerShape(6.dp))
                            .clickable { editorState.zoomScale = 1.0f }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(editorState.zoomScale * 100).toInt()}%",
                            color = Color(0xFF00E5FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Zoom In
                    IconButton(
                        onClick = { editorState.zoomIn() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.LightGray)
                    }
                }

                // Snap increment selector badge (Bottom Left of Canvas)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color(0xFF161B22).copy(alpha = 0.90f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SNAP:", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    if (!editorState.enableBlockBetweenGrids) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2A2000), RoundedCornerShape(4.dp))
                                .border(BorderStroke(1.dp, Color(0xFFFFB300)), RoundedCornerShape(4.dp))
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Izgaralar arasına blok koymak için Ayarlar'dan 'Enable block between grids' seçeneğini açın.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "1.0x (STRICT GRID)",
                                color = Color(0xFFFFB300),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        listOf(0.25f, 0.5f, 1.0f).forEach { snapVal ->
                            val isSel = editorState.snapIncrement == snapVal
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSel) Color(0xFF00E5FF) else Color(0xFF21262D),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { editorState.snapIncrement = snapVal }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${snapVal}x",
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Scrubber
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22))
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROGRESS:",
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = editorState.scrollX.coerceIn(0f, editorState.levelEstimatedLength),
                    onValueChange = { editorState.scrollX = it },
                    valueRange = 0f..editorState.levelEstimatedLength,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${editorState.scrollX.toInt()}m / ${editorState.levelEstimatedLength.toInt()}m",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom Object Palette Tray
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
                        val isSelected = editorState.selectedCategory == cat
                        val iconLabel = when (cat) {
                            ObjectCategory.BLOCKS -> "🧱 Blocks"
                            ObjectCategory.HAZARDS -> "⚠️ Hazards"
                            ObjectCategory.PADS -> "🚀 Pads"
                            ObjectCategory.ORBS -> "🔮 Orbs"
                            ObjectCategory.PORTALS -> "🌀 Portals"
                            ObjectCategory.TRIGGERS -> "⚡ Triggers"
                            ObjectCategory.DECORATION -> "✨ Deco"
                            ObjectCategory.SPECIAL -> "⭐ Special"
                        }
                        Tab(
                            selected = isSelected,
                            onClick = {
                                editorState.selectedCategory = cat
                                editorState.selectedType = ObjectType.values().first { it.category == cat }
                            },
                            text = {
                                Text(
                                    text = iconLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Available Objects in selected category - Visual Card Preview Grid
                val currentCategoryObjects = ObjectType.values().filter { it.category == editorState.selectedCategory }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(currentCategoryObjects) { type ->
                        val isSelected = editorState.selectedType == type
                        Card(
                            onClick = {
                                editorState.selectedType = type
                                editorState.activeTool = EditorTool.PLACE
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF21262D) else Color(0xFF161B22)
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF30363D)
                            ),
                            modifier = Modifier
                                .width(68.dp)
                                .height(64.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Mini live Canvas representation of the object
                                Canvas(modifier = Modifier.size(32.dp)) {
                                    val dummyObj = GameObject(0f, 0f, type)
                                    val scale = size.height / (type.height.coerceAtLeast(type.width) * 1.25f)
                                    val groundOffset = size.height * 0.95f
                                    GameRenderer.drawGameObject(
                                        scope = this,
                                        obj = dummyObj,
                                        cameraX = 0f,
                                        groundScreenY = groundOffset,
                                        tileSize = scale,
                                        sawAngle = editorSawAngle,
                                        isEditor = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = type.displayName.take(8),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.LightGray,
                                    maxLines = 1
                                )
                            }
                        }
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
                border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editorState.levelDescription,
                        onValueChange = { editorState.levelDescription = it },
                        label = { Text("Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Music selection
                    Text("Soundtrack:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val songNames = listOf("Stereo Track", "Cyber Beat", "Dark Demon", "Neon Pulse")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        songNames.forEachIndexed { idx, name ->
                            val isSel = editorState.musicTrack == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSel) Color(0xFF00E5FF) else Color(0xFF21262D),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editorState.musicTrack = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(6),
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Background Color Palettes
                    Text("Background Color:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val bgColors = listOf(
                        0xFF0D1117L to "Midnight",
                        0xFF1A0A2AL to "Abyss",
                        0xFF2A0808L to "Demon",
                        0xFF051821L to "Cyan",
                        0xFF121212L to "Charcoal"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bgColors.forEach { (c, name) ->
                            val isSel = editorState.bgColor == c
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .background(Color(c), RoundedCornerShape(6.dp))
                                    .border(
                                        BorderStroke(
                                            if (isSel) 2.5.dp else 1.dp,
                                            if (isSel) Color(0xFF00E5FF) else Color.DarkGray
                                        ),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { editorState.bgColor = c },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.take(3), color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target Difficulty
                    Text("Target Difficulty:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD, Difficulty.HARDER, Difficulty.INSANE, Difficulty.DEMON).forEach { d ->
                            val isSel = editorState.difficulty == d
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSel) Color(d.colorHex) else Color(0xFF21262D),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        editorState.difficulty = d
                                        editorState.stars = d.defaultStars
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.displayName.take(3),
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Enable block between grids Switch
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                        border = BorderStroke(1.dp, if (editorState.enableBlockBetweenGrids) Color(0xFF00E5FF) else Color(0xFF30363D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable block between grids",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (editorState.enableBlockBetweenGrids)
                                        "Izgaralar arasına blok koyma AÇIK (0.5x, 0.25x sub-grid)"
                                    else
                                        "Izgaralar arasına blok koyma KAPALI (Sadece tam 1.0x ızgara)",
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )
                            }
                            Switch(
                                checked = editorState.enableBlockBetweenGrids,
                                onCheckedChange = { editorState.enableBlockBetweenGrids = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = Color(0xFF00E5FF),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color(0xFF21262D)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level Target Length (Uzun Leveller)
                    Text("Level Length (Uzunluk / Süre):", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val lengthPresets = listOf(
                        300f to "300m (~28s)",
                        600f to "600m (~58s)",
                        1200f to "1.2km (~2m)",
                        2000f to "2.0km (~3m+)"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        lengthPresets.forEach { (len, label) ->
                            val isSel = (editorState.customLevelLength ?: 300f) == len
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSel) Color(0xFF00E5FF) else Color(0xFF21262D),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { editorState.customLevelLength = len }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label.split(" ").first(),
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Editor Zoom Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Editor Zoom (Yakınlaştırma):", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${(editorState.zoomScale * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = editorState.zoomScale,
                        onValueChange = { editorState.zoomScale = it },
                        valueRange = 0.35f..2.2f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

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
                        Text("CLEAR ALL OBJECTS", color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
