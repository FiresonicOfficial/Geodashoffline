package com.example.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.Difficulty
import com.example.model.GameObject
import com.example.model.Level
import com.example.model.ObjectCategory
import com.example.model.ObjectType
import kotlin.math.floor
import kotlin.math.round

enum class EditorTool {
    PLACE,
    ERASE
}

class LevelEditorState(initialLevel: Level? = null) {
    var levelId by mutableStateOf(initialLevel?.id ?: ("custom_" + System.currentTimeMillis()))
    var levelName by mutableStateOf(initialLevel?.name ?: "My Custom Level")
    var levelDescription by mutableStateOf(initialLevel?.description ?: "Created with GeoDash Editor")
    var difficulty by mutableStateOf(initialLevel?.difficulty ?: Difficulty.NORMAL)
    var stars by mutableStateOf(initialLevel?.stars ?: 3)
    var musicTrack by mutableStateOf(initialLevel?.musicTrack ?: 0)
    var bgColor by mutableStateOf(initialLevel?.bgColor ?: 0xFF0D1117)
    var groundColor by mutableStateOf(initialLevel?.groundColor ?: 0xFF003366)

    val objects = mutableStateListOf<GameObject>().apply {
        if (initialLevel != null) {
            addAll(initialLevel.objects)
        }
    }

    var selectedCategory by mutableStateOf(ObjectCategory.BLOCKS)
    var selectedType by mutableStateOf(ObjectType.BLOCK)
    var activeTool by mutableStateOf(EditorTool.PLACE)

    // Viewport scrolling & zooming
    var scrollX by mutableFloatStateOf(0f)
    var zoomScale by mutableFloatStateOf(1.0f)
    var snapIncrement by mutableFloatStateOf(0.5f)

    val maxObjectX: Float get() = objects.maxOfOrNull { it.x } ?: 0f
    val levelEstimatedLength: Float get() = (maxObjectX + 20f).coerceAtLeast(60f)

    fun zoomIn() {
        zoomScale = (zoomScale + 0.15f).coerceAtMost(1.75f)
    }

    fun zoomOut() {
        zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.65f)
    }

    fun jumpToStart() {
        scrollX = 0f
    }

    fun jumpToEnd() {
        scrollX = (maxObjectX - 6f).coerceAtLeast(0f)
    }

    // Undo / Redo history
    private val undoStack = mutableListOf<List<GameObject>>()
    private val redoStack = mutableListOf<List<GameObject>>()

    fun pushHistory() {
        undoStack.add(objects.toList())
        if (undoStack.size > 30) undoStack.removeAt(0)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(objects.toList())
            val prev = undoStack.removeAt(undoStack.size - 1)
            objects.clear()
            objects.addAll(prev)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(objects.toList())
            val next = redoStack.removeAt(redoStack.size - 1)
            objects.clear()
            objects.addAll(next)
        }
    }

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun onGridTapped(rawGridX: Float, rawGridY: Float) {
        val snappedX = (round(rawGridX / snapIncrement) * snapIncrement).coerceAtLeast(0f)
        val snappedY = (round(rawGridY / snapIncrement) * snapIncrement).coerceIn(0f, 8f)

        if (activeTool == EditorTool.PLACE) {
            pushHistory()
            // Remove any overlapping object at exact position
            objects.removeAll { abs(it.x - snappedX) < 0.25f && abs(it.y - snappedY) < 0.25f }
            objects.add(GameObject(x = snappedX, y = snappedY, type = selectedType))
        } else if (activeTool == EditorTool.ERASE) {
            val target = objects.firstOrNull {
                snappedX >= it.x && snappedX <= it.x + it.type.width &&
                        snappedY >= it.y && snappedY <= it.y + it.type.height
            }
            if (target != null) {
                pushHistory()
                objects.remove(target)
            }
        }
    }

    fun clearAll() {
        pushHistory()
        objects.clear()
    }

    fun buildLevel(): Level {
        return Level(
            id = levelId,
            name = levelName.ifBlank { "Untitled Level" },
            description = levelDescription,
            difficulty = difficulty,
            stars = stars,
            author = "Player",
            isCustom = true,
            musicTrack = musicTrack,
            bgColor = bgColor,
            groundColor = groundColor,
            objects = objects.sortedBy { it.x },
            createdAt = System.currentTimeMillis()
        )
    }

    private fun abs(v: Float) = if (v < 0) -v else v
}
