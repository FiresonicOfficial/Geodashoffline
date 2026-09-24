package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.GameObject
import com.example.model.ObjectType

object GameRenderer {

    // Reusable Path caches to completely avoid per-frame garbage collection
    private val spikePath = Path()
    private val hangingSpikePath = Path()
    private val shipPath = Path()
    private val wavePath = Path()
    private val ufoPath = Path()
    private val diamondPath = Path()

    fun renderGame(
        scope: DrawScope,
        engine: GameEngine,
        beatPulse: Float,
        cameraX: Float
    ) {
        val width = scope.size.width
        val height = scope.size.height

        // Unit scale: 1 grid unit = tileSize px
        val tileSize = (height / 10.5f)
        val groundScreenY = height - (tileSize * 1.8f)

        // 1. Draw rhythmic background
        drawBackground(scope, engine, beatPulse, cameraX, width, groundScreenY)

        // 2. Draw ground and neon grid lines
        drawGround(scope, engine, beatPulse, cameraX, width, height, groundScreenY, tileSize)

        // 3. Draw game objects in active camera viewport only
        val minX = cameraX - 2f
        val maxX = cameraX + (width / tileSize) + 2.5f
        val sawAngle = engine.sawRotation
        for (obj in engine.sortedObjects) {
            if (obj.x > maxX) break
            if (obj.x + obj.type.width < minX) continue
            // Triggers are completely invisible during gameplay (only visible in editor)
            if (obj.type.category == com.example.model.ObjectCategory.TRIGGERS) continue
            drawGameObject(scope, obj, cameraX, groundScreenY, tileSize, sawAngle, isEditor = false)
        }

        // 4. Draw practice mode checkpoints
        if (engine.isPracticeMode) {
            for (cp in engine.checkpoints) {
                if (cp.x in minX..maxX) {
                    drawCheckpointDiamond(scope, cp.x, cp.y, cameraX, groundScreenY, tileSize)
                }
            }
        }

        // 5. Draw particles & ring pulses
        drawParticles(scope, engine, cameraX, groundScreenY, tileSize)

        // 6. Draw player
        if (!engine.isDead) {
            drawPlayer(scope, engine, cameraX, groundScreenY, tileSize)
        }
    }

    private fun drawBackground(
        scope: DrawScope,
        engine: GameEngine,
        beatPulse: Float,
        cameraX: Float,
        width: Float,
        groundScreenY: Float
    ) {
        val baseBg = Color(engine.currentBgColor)
        val pulseFactor = (beatPulse * 0.18f)
        val topColor = Color(
            red = (baseBg.red + pulseFactor).coerceIn(0f, 1f),
            green = (baseBg.green + pulseFactor).coerceIn(0f, 1f),
            blue = (baseBg.blue + pulseFactor).coerceIn(0f, 1f),
            alpha = 1f
        )
        val bottomColor = Color(
            red = (baseBg.red * 0.5f).coerceIn(0f, 1f),
            green = (baseBg.green * 0.5f).coerceIn(0f, 1f),
            blue = (baseBg.blue * 0.5f).coerceIn(0f, 1f),
            alpha = 1f
        )

        // Gradient background
        scope.drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(topColor, bottomColor),
                startY = 0f,
                endY = groundScreenY
            ),
            size = Size(width, groundScreenY)
        )

        // Parallax background grid elements
        val bgOffset = (cameraX * 8f) % 120f
        val gridColor = Color.White.copy(alpha = 0.04f + (beatPulse * 0.03f))
        var curX = -bgOffset
        while (curX < width) {
            scope.drawLine(
                color = gridColor,
                start = Offset(curX, 0f),
                end = Offset(curX, groundScreenY),
                strokeWidth = 1.5f
            )
            curX += 120f
        }
    }

    private fun drawGround(
        scope: DrawScope,
        engine: GameEngine,
        beatPulse: Float,
        cameraX: Float,
        width: Float,
        height: Float,
        groundScreenY: Float,
        tileSize: Float
    ) {
        val groundBaseColor = Color(engine.currentGroundColor)
        val groundGlowColor = Color(
            red = (groundBaseColor.red + beatPulse * 0.25f).coerceIn(0f, 1f),
            green = (groundBaseColor.green + beatPulse * 0.25f).coerceIn(0f, 1f),
            blue = (groundBaseColor.blue + beatPulse * 0.25f).coerceIn(0f, 1f)
        )

        // Ground body
        scope.drawRect(
            color = groundBaseColor,
            topLeft = Offset(0f, groundScreenY),
            size = Size(width, height - groundScreenY)
        )

        // Neon glowing top edge
        scope.drawLine(
            color = groundGlowColor,
            start = Offset(0f, groundScreenY),
            end = Offset(width, groundScreenY),
            strokeWidth = 4f
        )
        scope.drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = Offset(0f, groundScreenY),
            end = Offset(width, groundScreenY),
            strokeWidth = 1.5f
        )

        // Scrolling ground vertical lines
        val scrollOffset = (cameraX * tileSize) % tileSize
        val lineColor = Color.White.copy(alpha = 0.12f)
        var x = -scrollOffset
        while (x < width) {
            scope.drawLine(
                color = lineColor,
                start = Offset(x, groundScreenY),
                end = Offset(x, height),
                strokeWidth = 2f
            )
            x += tileSize
        }

        // Horizontal ground accent line
        scope.drawLine(
            color = lineColor,
            start = Offset(0f, groundScreenY + tileSize * 0.6f),
            end = Offset(width, groundScreenY + tileSize * 0.6f),
            strokeWidth = 1.5f
        )
    }

    fun drawGameObject(
        scope: DrawScope,
        obj: GameObject,
        cameraX: Float,
        groundScreenY: Float,
        tileSize: Float,
        sawAngle: Float = 0f,
        isEditor: Boolean = false
    ) {
        // Triggers are only visible in the editor, completely invisible during gameplay!
        if (!isEditor && obj.type.category == com.example.model.ObjectCategory.TRIGGERS) {
            return
        }

        val screenX = (obj.x - cameraX) * tileSize
        val screenY = groundScreenY - ((obj.y + obj.type.height) * tileSize)
        val objW = obj.type.width * tileSize
        val objH = obj.type.height * tileSize

        when (obj.type) {
            ObjectType.BLOCK, ObjectType.BLOCK_DARK, ObjectType.BLOCK_GRID, ObjectType.HALF_BLOCK -> {
                val blockColor = Color(obj.type.primaryColorHex)
                scope.drawRect(
                    color = blockColor.copy(alpha = 0.85f),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH)
                )
                scope.drawRect(
                    color = Color.White.copy(alpha = 0.8f),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    style = Stroke(width = 2.5f)
                )
                if (objH >= tileSize * 0.8f) {
                    val inset = 6f
                    scope.drawRect(
                        color = Color.Black.copy(alpha = 0.35f),
                        topLeft = Offset(screenX + inset, screenY + inset),
                        size = Size(objW - inset * 2, objH - inset * 2)
                    )
                }
            }

            ObjectType.BLOCK_RAINBOW -> {
                scope.drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF007F), Color(0xFFFF9100), Color(0xFF00E5FF), Color(0xFF7C4DFF)),
                        start = Offset(screenX, screenY),
                        end = Offset(screenX + objW, screenY + objH)
                    ),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH)
                )
                scope.drawRect(
                    color = Color.White,
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    style = Stroke(width = 2.5f)
                )
            }

            ObjectType.BLOCK_OUTLINE -> {
                scope.drawRect(
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH)
                )
                scope.drawRect(
                    color = Color(0xFF00E5FF),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    style = Stroke(width = 2.5f)
                )
                scope.drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    start = Offset(screenX, screenY),
                    end = Offset(screenX + objW, screenY + objH),
                    strokeWidth = 1.5f
                )
            }

            ObjectType.SPIKE, ObjectType.SPIKE_SMALL, ObjectType.SPIKE_DUAL -> {
                spikePath.reset()
                spikePath.moveTo(screenX, screenY + objH)
                spikePath.lineTo(screenX + objW * 0.5f, screenY)
                spikePath.lineTo(screenX + objW, screenY + objH)
                spikePath.close()

                scope.drawPath(
                    path = spikePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF1744), Color(0xFF7F0000)),
                        startY = screenY,
                        endY = screenY + objH
                    )
                )
                scope.drawPath(
                    path = spikePath,
                    color = Color(0xFFFF8A80),
                    style = Stroke(width = 2.5f)
                )
            }

            ObjectType.SPIKE_TRIPLE, ObjectType.SPIKE_FOUR -> {
                val spikeCount = if (obj.type == ObjectType.SPIKE_TRIPLE) 3 else 4
                val singleSpikeW = objW / spikeCount
                for (s in 0 until spikeCount) {
                    val sx = screenX + (s * singleSpikeW)
                    spikePath.reset()
                    spikePath.moveTo(sx, screenY + objH)
                    spikePath.lineTo(sx + singleSpikeW * 0.5f, screenY)
                    spikePath.lineTo(sx + singleSpikeW, screenY + objH)
                    spikePath.close()

                    scope.drawPath(
                        path = spikePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF1744), Color(0xFF7F0000)),
                            startY = screenY,
                            endY = screenY + objH
                        )
                    )
                    scope.drawPath(
                        path = spikePath,
                        color = Color(0xFFFF8A80),
                        style = Stroke(width = 2.5f)
                    )
                }
            }

            ObjectType.SAWBLADE_GIANT, ObjectType.SAWBLADE_LARGE, ObjectType.SAWBLADE_MEDIUM, ObjectType.SAWBLADE_SMALL -> {
                val center = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val radius = objW * 0.46f

                // Outer teeth rotation
                scope.rotate(sawAngle, center) {
                    val toothCount = when (obj.type) {
                        ObjectType.SAWBLADE_GIANT -> 16
                        ObjectType.SAWBLADE_LARGE -> 12
                        else -> 8
                    }
                    val angleStep = 360f / toothCount
                    for (i in 0 until toothCount) {
                        rotate(i * angleStep, center) {
                            val bladePath = Path()
                            bladePath.moveTo(center.x - radius * 0.2f, center.y - radius * 0.8f)
                            bladePath.lineTo(center.x, center.y - radius * 1.08f)
                            bladePath.lineTo(center.x + radius * 0.2f, center.y - radius * 0.8f)
                            bladePath.close()
                            drawPath(bladePath, color = Color(0xFFFF5722))
                        }
                    }

                    // Main blade disc
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF9E80), Color(0xFFD84315), Color(0xFF3E2723)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius * 0.86f,
                        center = center
                    )

                    // Sharp outer rim
                    drawCircle(
                        color = Color(0xFFFFCCBC),
                        radius = radius * 0.86f,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )

                    // Inner saw vents / slots
                    for (i in 0 until 4) {
                        rotate(i * 90f + 45f, center) {
                            drawLine(
                                color = Color.Black.copy(alpha = 0.5f),
                                start = Offset(center.x, center.y - radius * 0.65f),
                                end = Offset(center.x, center.y - radius * 0.25f),
                                strokeWidth = 3f
                            )
                        }
                    }

                    // Central metallic hub
                    drawCircle(
                        color = Color(0xFF263238),
                        radius = radius * 0.32f,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFFF5722),
                        radius = radius * 0.16f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = radius * 0.08f,
                        center = center
                    )
                }
            }

            ObjectType.SPIKE_HANGING -> {
                hangingSpikePath.reset()
                hangingSpikePath.moveTo(screenX, screenY)
                hangingSpikePath.lineTo(screenX + objW * 0.5f, screenY + objH)
                hangingSpikePath.lineTo(screenX + objW, screenY)
                hangingSpikePath.close()

                scope.drawPath(
                    path = hangingSpikePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7F0000), Color(0xFFFF1744)),
                        startY = screenY,
                        endY = screenY + objH
                    )
                )
                scope.drawPath(
                    path = hangingSpikePath,
                    color = Color(0xFFFF8A80),
                    style = Stroke(width = 2.5f)
                )
            }

            ObjectType.PAD_YELLOW, ObjectType.PAD_PINK, ObjectType.PAD_RED, ObjectType.PAD_PURPLE, ObjectType.PAD_GRAVITY -> {
                val padColor = Color(obj.type.primaryColorHex)
                scope.drawRoundRect(
                    color = padColor,
                    topLeft = Offset(screenX + 4f, screenY + objH * 0.2f),
                    size = Size(objW - 8f, objH * 0.8f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
                scope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(screenX + 8f, screenY + objH * 0.4f),
                    size = Size(objW - 16f, objH * 0.4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }

            ObjectType.ORB_YELLOW, ObjectType.ORB_PINK, ObjectType.ORB_BLUE, ObjectType.ORB_GREEN,
            ObjectType.ORB_RED, ObjectType.ORB_BLACK, ObjectType.ORB_DASH, ObjectType.ORB_RAINBOW -> {
                val orbColor = Color(obj.type.primaryColorHex)
                val center = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val radius = objW * 0.45f

                scope.drawCircle(
                    color = orbColor.copy(alpha = 0.35f),
                    radius = radius * 1.25f,
                    center = center,
                    style = Stroke(width = 3f)
                )
                scope.drawCircle(
                    color = orbColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 4f)
                )
                scope.drawCircle(
                    color = if (obj.type == ObjectType.ORB_BLACK) Color(0xFF1A0033) else orbColor.copy(alpha = 0.8f),
                    radius = radius * 0.55f,
                    center = center
                )
                scope.drawCircle(
                    color = if (obj.type == ObjectType.ORB_BLACK) Color(0xFFD500F9) else Color.White,
                    radius = radius * 0.25f,
                    center = center
                )
            }

            // Triggers (Color changes for BG and Ground)
            ObjectType.TRIGGER_BG_CYAN, ObjectType.TRIGGER_BG_PURPLE, ObjectType.TRIGGER_BG_RED,
            ObjectType.TRIGGER_BG_DARK, ObjectType.TRIGGER_BG_GREEN, ObjectType.TRIGGER_BG_ORANGE,
            ObjectType.TRIGGER_GROUND_BLUE, ObjectType.TRIGGER_GROUND_PURPLE, ObjectType.TRIGGER_GROUND_RED,
            ObjectType.TRIGGER_GROUND_GREEN, ObjectType.TRIGGER_GROUND_DARK, ObjectType.TRIGGER_GROUND_GOLD -> {
                val triggerColor = Color(obj.customColorHex ?: obj.type.primaryColorHex)
                val isBg = obj.type.name.startsWith("TRIGGER_BG_")

                // Vertical light beam
                scope.drawLine(
                    color = triggerColor.copy(alpha = 0.45f),
                    start = Offset(screenX + objW * 0.5f, screenY - tileSize * 4f),
                    end = Offset(screenX + objW * 0.5f, screenY + objH + tileSize),
                    strokeWidth = 3f
                )

                // Hologram trigger box
                scope.drawRoundRect(
                    color = Color.Black.copy(alpha = 0.7f),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                scope.drawRoundRect(
                    color = triggerColor,
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    style = Stroke(width = 2.5f)
                )

                // Inner core ring
                scope.drawCircle(
                    color = triggerColor,
                    radius = objW * 0.28f,
                    center = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                )
                scope.drawCircle(
                    color = Color.White,
                    radius = objW * 0.12f,
                    center = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                )
            }

            ObjectType.PORTAL_SHIP, ObjectType.PORTAL_CUBE,
            ObjectType.PORTAL_WAVE, ObjectType.PORTAL_UFO,
            ObjectType.PORTAL_GRAVITY_INVERT, ObjectType.PORTAL_GRAVITY_NORMAL,
            ObjectType.PORTAL_SPEED_0_5X, ObjectType.PORTAL_SPEED_1X, ObjectType.PORTAL_SPEED_2X,
            ObjectType.PORTAL_SPEED_3X, ObjectType.PORTAL_SPEED_4X -> {
                val portalColor = Color(obj.type.primaryColorHex)
                scope.drawOval(
                    color = portalColor.copy(alpha = 0.35f),
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH)
                )
                scope.drawOval(
                    color = portalColor,
                    topLeft = Offset(screenX, screenY),
                    size = Size(objW, objH),
                    style = Stroke(width = 4f)
                )
                scope.drawOval(
                    color = Color.White,
                    topLeft = Offset(screenX + objW * 0.25f, screenY + objH * 0.1f),
                    size = Size(objW * 0.5f, objH * 0.8f),
                    style = Stroke(width = 2f)
                )

                // Portal inner mode icon/symbol
                when (obj.type) {
                    ObjectType.PORTAL_WAVE -> {
                        // Sharp zigzag wave glyph inside portal
                        val pMidY = screenY + objH * 0.5f
                        val pMidX = screenX + objW * 0.5f
                        wavePath.reset()
                        wavePath.moveTo(pMidX - objW * 0.2f, pMidY + objH * 0.1f)
                        wavePath.lineTo(pMidX, pMidY - objH * 0.1f)
                        wavePath.lineTo(pMidX + objW * 0.2f, pMidY + objH * 0.1f)
                        scope.drawPath(wavePath, color = Color.White, style = Stroke(width = 3f))
                    }
                    ObjectType.PORTAL_UFO -> {
                        // Flying saucer disc glyph inside portal
                        val pMidY = screenY + objH * 0.5f
                        val pMidX = screenX + objW * 0.5f
                        scope.drawOval(
                            color = Color.White,
                            topLeft = Offset(pMidX - objW * 0.25f, pMidY - objH * 0.08f),
                            size = Size(objW * 0.5f, objH * 0.16f)
                        )
                    }
                    ObjectType.PORTAL_SHIP -> {
                        val pMidY = screenY + objH * 0.5f
                        val pMidX = screenX + objW * 0.5f
                        scope.drawLine(
                            color = Color.White,
                            start = Offset(pMidX - objW * 0.2f, pMidY),
                            end = Offset(pMidX + objW * 0.2f, pMidY),
                            strokeWidth = 3f
                        )
                    }
                    else -> {}
                }
            }

            ObjectType.COIN -> {
                val center = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val coinRadius = objW * 0.4f
                scope.drawCircle(
                    color = Color(0xFFFFD700),
                    radius = coinRadius,
                    center = center
                )
                scope.drawCircle(
                    color = Color(0xFFFFA000),
                    radius = coinRadius * 0.8f,
                    center = center,
                    style = Stroke(width = 3f)
                )
                scope.drawCircle(
                    color = Color.White,
                    radius = coinRadius * 0.35f,
                    center = center
                )
            }

            // Decorative hitbox-free objects
            ObjectType.DECO_CHAIN -> {
                val chainW = (objW * 0.65f).coerceAtLeast(6f)
                val linkH = tileSize * 0.45f
                val chainX = screenX + (objW - chainW) * 0.5f
                var curY = screenY
                while (curY < screenY + objH) {
                    val curH = linkH.coerceAtMost(screenY + objH - curY)
                    scope.drawRoundRect(
                        color = Color(0xFF78909C),
                        topLeft = Offset(chainX, curY),
                        size = Size(chainW, curH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                        style = Stroke(width = 2.5f)
                    )
                    scope.drawCircle(
                        color = Color(0xFFCFD8DC),
                        radius = 2f,
                        center = Offset(chainX + chainW * 0.5f, curY + curH * 0.5f)
                    )
                    curY += linkH * 0.72f
                }
            }

            ObjectType.DECO_PILLAR -> {
                val pillarColor = Color(0xFF263238).copy(alpha = 0.55f)
                val highlightColor = Color(0xFF546E7A).copy(alpha = 0.45f)
                // Capital top & base
                scope.drawRect(color = highlightColor, topLeft = Offset(screenX, screenY), size = Size(objW, 8f))
                scope.drawRect(color = highlightColor, topLeft = Offset(screenX, screenY + objH - 8f), size = Size(objW, 8f))
                // Shaft
                scope.drawRect(color = pillarColor, topLeft = Offset(screenX + 3f, screenY + 8f), size = Size(objW - 6f, objH - 16f))
                // Fluting lines
                val fluteStep = (objW - 6f) / 3f
                for (i in 1..2) {
                    scope.drawLine(
                        color = highlightColor,
                        start = Offset(screenX + 3f + i * fluteStep, screenY + 8f),
                        end = Offset(screenX + 3f + i * fluteStep, screenY + objH - 8f),
                        strokeWidth = 1.5f
                    )
                }
            }

            ObjectType.DECO_NEON_ARROW -> {
                val arrowColor = Color(obj.customColorHex ?: 0xFF00E5FF)
                val centerY = screenY + objH * 0.5f
                for (offset in listOf(0f, objW * 0.38f)) {
                    val startX = screenX + offset + objW * 0.12f
                    val arrowPath = Path().apply {
                        moveTo(startX, screenY + objH * 0.15f)
                        lineTo(startX + objW * 0.35f, centerY)
                        lineTo(startX, screenY + objH * 0.85f)
                    }
                    scope.drawPath(arrowPath, color = arrowColor.copy(alpha = 0.25f), style = Stroke(width = 6f))
                    scope.drawPath(arrowPath, color = arrowColor, style = Stroke(width = 2.5f))
                    scope.drawPath(arrowPath, color = Color.White, style = Stroke(width = 1f))
                }
            }

            ObjectType.DECO_ARROW_UP, ObjectType.DECO_ARROW_DOWN -> {
                val isUp = obj.type == ObjectType.DECO_ARROW_UP
                val arrowCol = Color(obj.customColorHex ?: if (isUp) 0xFF00E676 else 0xFFFF1744)
                val centerX = screenX + objW * 0.5f
                val arrowPath = Path().apply {
                    if (isUp) {
                        moveTo(screenX + objW * 0.15f, screenY + objH * 0.78f)
                        lineTo(centerX, screenY + objH * 0.22f)
                        lineTo(screenX + objW * 0.85f, screenY + objH * 0.78f)
                    } else {
                        moveTo(screenX + objW * 0.15f, screenY + objH * 0.22f)
                        lineTo(centerX, screenY + objH * 0.78f)
                        lineTo(screenX + objW * 0.85f, screenY + objH * 0.22f)
                    }
                }
                scope.drawPath(arrowPath, color = arrowCol.copy(alpha = 0.3f), style = Stroke(width = 6f))
                scope.drawPath(arrowPath, color = arrowCol, style = Stroke(width = 2.8f))
                scope.drawPath(arrowPath, color = Color.White, style = Stroke(width = 1.2f))
            }

            ObjectType.DECO_WARNING_SIGN -> {
                val signPath = Path().apply {
                    moveTo(screenX + objW * 0.5f, screenY + objH * 0.08f)
                    lineTo(screenX + objW * 0.94f, screenY + objH * 0.92f)
                    lineTo(screenX + objW * 0.06f, screenY + objH * 0.92f)
                    close()
                }
                scope.drawPath(signPath, color = Color(0xFFFFD600).copy(alpha = 0.25f))
                scope.drawPath(signPath, color = Color(0xFFFFD600), style = Stroke(width = 2.5f))
                val midX = screenX + objW * 0.5f
                scope.drawLine(
                    color = Color(0xFFFFD600),
                    start = Offset(midX, screenY + objH * 0.35f),
                    end = Offset(midX, screenY + objH * 0.65f),
                    strokeWidth = 3f
                )
                scope.drawCircle(
                    color = Color(0xFFFFD600),
                    radius = 2.5f,
                    center = Offset(midX, screenY + objH * 0.78f)
                )
            }

            ObjectType.DECO_STAR -> {
                val starCenter = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val r = objW * 0.45f
                val starPath = Path().apply {
                    moveTo(starCenter.x, starCenter.y - r)
                    quadraticBezierTo(starCenter.x, starCenter.y, starCenter.x + r, starCenter.y)
                    quadraticBezierTo(starCenter.x, starCenter.y, starCenter.x, starCenter.y + r)
                    quadraticBezierTo(starCenter.x, starCenter.y, starCenter.x - r, starCenter.y)
                    quadraticBezierTo(starCenter.x, starCenter.y, starCenter.x, starCenter.y - r)
                    close()
                }
                scope.drawCircle(color = Color(0xFFFFF59D).copy(alpha = 0.25f), radius = r * 1.3f, center = starCenter)
                scope.drawPath(starPath, color = Color(0xFFFFF9C4))
                scope.drawCircle(color = Color.White, radius = r * 0.28f, center = starCenter)
            }

            ObjectType.DECO_PULSE_RING -> {
                val ringCenter = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val maxR = objW * 0.45f
                scope.drawCircle(color = Color(0xFFE040FB).copy(alpha = 0.18f), radius = maxR, center = ringCenter)
                scope.drawCircle(color = Color(0xFFE040FB), radius = maxR, center = ringCenter, style = Stroke(width = 2.2f))
                scope.drawCircle(color = Color(0xFF00E5FF), radius = maxR * 0.6f, center = ringCenter, style = Stroke(width = 1.5f))
                scope.drawCircle(color = Color.White, radius = maxR * 0.22f, center = ringCenter)
            }

            ObjectType.DECO_TECH_CIRCUIT -> {
                val circuitColor = Color(0xFF2979FF).copy(alpha = 0.55f)
                val nodeColor = Color(0xFF00E5FF)
                scope.drawLine(color = circuitColor, start = Offset(screenX, screenY + objH * 0.3f), end = Offset(screenX + objW * 0.5f, screenY + objH * 0.3f), strokeWidth = 2f)
                scope.drawLine(color = circuitColor, start = Offset(screenX + objW * 0.5f, screenY + objH * 0.3f), end = Offset(screenX + objW * 0.8f, screenY + objH * 0.7f), strokeWidth = 2f)
                scope.drawLine(color = circuitColor, start = Offset(screenX + objW * 0.8f, screenY + objH * 0.7f), end = Offset(screenX + objW, screenY + objH * 0.7f), strokeWidth = 2f)
                scope.drawCircle(color = nodeColor, radius = 3.5f, center = Offset(screenX + objW * 0.5f, screenY + objH * 0.3f))
                scope.drawCircle(color = nodeColor, radius = 3.5f, center = Offset(screenX + objW * 0.8f, screenY + objH * 0.7f))
            }

            ObjectType.DECO_BUSH -> {
                val bushColor = Color(0xFF00E676).copy(alpha = 0.4f)
                val outlineColor = Color(0xFF69F0AE)
                val tufts = 3
                val tuftW = objW / tufts
                for (i in 0 until tufts) {
                    val cx = screenX + (i + 0.5f) * tuftW
                    val cy = screenY + objH * 0.5f
                    val r = tuftW * 0.55f
                    scope.drawCircle(color = bushColor, radius = r, center = Offset(cx, cy))
                    scope.drawCircle(color = outlineColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.5f))
                }
            }

            ObjectType.DECO_MONSTER_EYE -> {
                val eyeCenter = Offset(screenX + objW * 0.5f, screenY + objH * 0.5f)
                val eyeW = objW * 0.46f
                val eyeH = objH * 0.32f
                scope.drawOval(
                    color = Color(0xFF140505),
                    topLeft = Offset(eyeCenter.x - eyeW, eyeCenter.y - eyeH),
                    size = Size(eyeW * 2, eyeH * 2)
                )
                scope.drawOval(
                    color = Color(0xFFFF1744),
                    topLeft = Offset(eyeCenter.x - eyeW * 0.75f, eyeCenter.y - eyeH * 0.8f),
                    size = Size(eyeW * 1.5f, eyeH * 1.6f)
                )
                scope.drawOval(
                    color = Color.Black,
                    topLeft = Offset(eyeCenter.x - 2.5f, eyeCenter.y - eyeH * 0.75f),
                    size = Size(5f, eyeH * 1.5f)
                )
                scope.drawCircle(
                    color = Color.White,
                    radius = 2.2f,
                    center = Offset(eyeCenter.x - eyeW * 0.25f, eyeCenter.y - eyeH * 0.3f)
                )
            }

            ObjectType.DECO_CRYSTAL -> {
                val crystalColor = Color(0xFFD500F9).copy(alpha = 0.6f)
                val crystalOutline = Color(0xFFEA80FC)
                val crystalPath = Path().apply {
                    moveTo(screenX + objW * 0.5f, screenY)
                    lineTo(screenX + objW * 0.85f, screenY + objH * 0.6f)
                    lineTo(screenX + objW * 0.65f, screenY + objH)
                    lineTo(screenX + objW * 0.35f, screenY + objH)
                    lineTo(screenX + objW * 0.15f, screenY + objH * 0.6f)
                    close()
                }
                scope.drawPath(crystalPath, color = crystalColor)
                scope.drawPath(crystalPath, color = crystalOutline, style = Stroke(width = 2f))
                scope.drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(screenX + objW * 0.5f, screenY),
                    end = Offset(screenX + objW * 0.5f, screenY + objH * 0.75f),
                    strokeWidth = 1.5f
                )
            }
        }
    }

    private fun drawCheckpointDiamond(
        scope: DrawScope,
        cx: Float,
        cy: Float,
        cameraX: Float,
        groundScreenY: Float,
        tileSize: Float
    ) {
        val sx = (cx - cameraX) * tileSize
        val sy = groundScreenY - (cy * tileSize)
        val size = tileSize * 0.8f

        diamondPath.reset()
        diamondPath.moveTo(sx + size * 0.5f, sy)
        diamondPath.lineTo(sx + size, sy + size * 0.5f)
        diamondPath.lineTo(sx + size * 0.5f, sy + size)
        diamondPath.lineTo(sx, sy + size * 0.5f)
        diamondPath.close()

        scope.drawPath(path = diamondPath, color = Color(0xFF00E676).copy(alpha = 0.6f))
        scope.drawPath(path = diamondPath, color = Color.White, style = Stroke(width = 2f))
    }

    private fun drawPlayer(
        scope: DrawScope,
        engine: GameEngine,
        cameraX: Float,
        groundScreenY: Float,
        tileSize: Float
    ) {
        val px = (engine.playerX - cameraX) * tileSize
        val py = groundScreenY - ((engine.playerY + 1f) * tileSize)
        val pSize = tileSize
        val center = Offset(px + pSize * 0.5f, py + pSize * 0.5f)

        scope.rotate(degrees = engine.playerRotation, pivot = center) {
            when (engine.gameMode) {
                PlayerGameMode.CUBE -> {
                    // Main yellow square body
                    scope.drawRect(
                        color = Color(0xFFFFE600),
                        topLeft = Offset(px, py),
                        size = Size(pSize, pSize)
                    )
                    // Dark outer border
                    scope.drawRect(
                        color = Color(0xFF0B0E1B),
                        topLeft = Offset(px, py),
                        size = Size(pSize, pSize),
                        style = Stroke(width = 3f)
                    )
                    // Inner cyan frame
                    val pad = pSize * 0.12f
                    scope.drawRect(
                        color = Color(0xFF00E5FF),
                        topLeft = Offset(px + pad, py + pad),
                        size = Size(pSize - pad * 2, pSize - pad * 2)
                    )
                    // Inner yellow core
                    val corePad = pSize * 0.22f
                    scope.drawRect(
                        color = Color(0xFFFFE600),
                        topLeft = Offset(px + corePad, py + corePad),
                        size = Size(pSize - corePad * 2, pSize - corePad * 2)
                    )
                    // Cyber eyes
                    val eyeW = pSize * 0.16f
                    val eyeH = pSize * 0.2f
                    scope.drawRect(
                        color = Color(0xFF0B0E1B),
                        topLeft = Offset(px + pSize * 0.25f, py + pSize * 0.25f),
                        size = Size(eyeW, eyeH)
                    )
                    scope.drawRect(
                        color = Color(0xFF0B0E1B),
                        topLeft = Offset(px + pSize * 0.6f, py + pSize * 0.25f),
                        size = Size(eyeW, eyeH)
                    )
                    // Cyber mouth
                    scope.drawRect(
                        color = Color(0xFF0B0E1B),
                        topLeft = Offset(px + pSize * 0.3f, py + pSize * 0.65f),
                        size = Size(pSize * 0.4f, pSize * 0.1f)
                    )
                }
                PlayerGameMode.SHIP -> {
                    // Ship mode: sleek rocket
                    shipPath.reset()
                    shipPath.moveTo(px + pSize, py + pSize * 0.5f) // nose
                    shipPath.lineTo(px, py + pSize * 0.15f) // top tail
                    shipPath.lineTo(px + pSize * 0.25f, py + pSize * 0.5f) // indent
                    shipPath.lineTo(px, py + pSize * 0.85f) // bottom tail
                    shipPath.close()

                    scope.drawPath(path = shipPath, color = Color(0xFFFF4081))
                    scope.drawPath(path = shipPath, color = Color.White, style = Stroke(width = 2.5f))

                    // Mini cube pilot inside cockpit
                    val cockpitSize = pSize * 0.35f
                    scope.drawRect(
                        color = Color(0xFFFFE600),
                        topLeft = Offset(px + pSize * 0.35f, py + pSize * 0.325f),
                        size = Size(cockpitSize, cockpitSize)
                    )
                }
                PlayerGameMode.WAVE -> {
                    // Wave mode: authentic sharp dart / arrow with glowing core
                    wavePath.reset()
                    wavePath.moveTo(px + pSize * 1.05f, py + pSize * 0.5f) // Sharp nose
                    wavePath.lineTo(px + pSize * 0.05f, py + pSize * 0.12f) // Top wing tip
                    wavePath.lineTo(px + pSize * 0.32f, py + pSize * 0.5f) // Inner back notch
                    wavePath.lineTo(px + pSize * 0.05f, py + pSize * 0.88f) // Bottom wing tip
                    wavePath.close()

                    // Electric cyan body with pure white outline
                    scope.drawPath(path = wavePath, color = Color(0xFF00E5FF))
                    scope.drawPath(path = wavePath, color = Color.White, style = Stroke(width = 2.5f))

                    // Inner glowing chevron stripe
                    val innerChevron = Path()
                    innerChevron.moveTo(px + pSize * 0.75f, py + pSize * 0.5f)
                    innerChevron.lineTo(px + pSize * 0.28f, py + pSize * 0.28f)
                    innerChevron.lineTo(px + pSize * 0.42f, py + pSize * 0.5f)
                    innerChevron.lineTo(px + pSize * 0.28f, py + pSize * 0.72f)
                    innerChevron.close()
                    scope.drawPath(path = innerChevron, color = Color.White)

                    // Glowing core star
                    scope.drawCircle(
                        color = Color(0xFF00B0FF),
                        radius = pSize * 0.10f,
                        center = Offset(px + pSize * 0.6f, py + pSize * 0.5f)
                    )
                }
                PlayerGameMode.UFO -> {
                    // UFO mode: authentic flying saucer with cockpit glass dome and pilot
                    val saucerWidth = pSize * 1.05f
                    val saucerHeight = pSize * 0.34f
                    val saucerTopY = py + pSize * 0.44f

                    // 1. Thruster glow at bottom
                    scope.drawOval(
                        color = Color(0xFFFF5722),
                        topLeft = Offset(px + pSize * 0.25f, py + pSize * 0.68f),
                        size = Size(pSize * 0.55f, pSize * 0.18f)
                    )
                    scope.drawCircle(
                        color = Color(0xFFFFEB3B),
                        radius = pSize * 0.09f,
                        center = Offset(px + pSize * 0.52f, py + pSize * 0.76f)
                    )

                    // 2. Glass cockpit dome arc
                    scope.drawArc(
                        color = Color(0xFF00E5FF).copy(alpha = 0.55f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(px + pSize * 0.22f, py + pSize * 0.12f),
                        size = Size(pSize * 0.6f, pSize * 0.5f)
                    )
                    scope.drawArc(
                        color = Color.White,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(px + pSize * 0.22f, py + pSize * 0.12f),
                        size = Size(pSize * 0.6f, pSize * 0.5f),
                        style = Stroke(width = 2f)
                    )

                    // 3. Mini cube pilot inside dome
                    val pilotSize = pSize * 0.22f
                    scope.drawRect(
                        color = Color(0xFFFFE600),
                        topLeft = Offset(px + pSize * 0.41f, py + pSize * 0.24f),
                        size = Size(pilotSize, pilotSize)
                    )
                    scope.drawRect(
                        color = Color(0xFF0B0E1B),
                        topLeft = Offset(px + pSize * 0.50f, py + pSize * 0.28f),
                        size = Size(pilotSize * 0.3f, pilotSize * 0.35f)
                    )

                    // 4. Main saucer hull (metallic disc with vibrant orange/amber rim)
                    scope.drawOval(
                        color = Color(0xFF263238),
                        topLeft = Offset(px, saucerTopY),
                        size = Size(saucerWidth, saucerHeight)
                    )
                    scope.drawOval(
                        color = Color(0xFFFF9100),
                        topLeft = Offset(px, saucerTopY),
                        size = Size(saucerWidth, saucerHeight),
                        style = Stroke(width = 3f)
                    )

                    // 5. Saucer rim lights
                    val lightRadius = pSize * 0.05f
                    scope.drawCircle(color = Color.White, radius = lightRadius, center = Offset(px + pSize * 0.2f, saucerTopY + saucerHeight * 0.5f))
                    scope.drawCircle(color = Color(0xFFFFEA00), radius = lightRadius, center = Offset(px + pSize * 0.52f, saucerTopY + saucerHeight * 0.5f))
                    scope.drawCircle(color = Color.White, radius = lightRadius, center = Offset(px + pSize * 0.85f, saucerTopY + saucerHeight * 0.5f))
                }
            }
        }
    }

    private fun drawParticles(
        scope: DrawScope,
        engine: GameEngine,
        cameraX: Float,
        groundScreenY: Float,
        tileSize: Float
    ) {
        // Shard and trail particles
        for (p in engine.particles) {
            val sx = (p.x - cameraX) * tileSize
            val sy = groundScreenY - (p.y * tileSize)
            val pColor = Color(p.colorHex).copy(alpha = p.alpha)
            scope.drawCircle(
                color = pColor,
                radius = p.size,
                center = Offset(sx, sy)
            )
        }

        // Orb trigger ripple waves
        for (ring in engine.orbRingEffects) {
            val sx = (ring.x - cameraX) * tileSize
            val sy = groundScreenY - (ring.y * tileSize)
            val ringRadius = ring.r * tileSize
            scope.drawCircle(
                color = Color.White.copy(alpha = (1f - (ring.r / 1.8f)).coerceIn(0f, 1f)),
                radius = ringRadius,
                center = Offset(sx, sy),
                style = Stroke(width = 3f)
            )
        }
    }
}
