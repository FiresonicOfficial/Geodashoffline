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
            drawGameObject(scope, obj, cameraX, groundScreenY, tileSize, sawAngle)
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
        sawAngle: Float = 0f
    ) {
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
            if (engine.gameMode == PlayerGameMode.CUBE) {
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
            } else {
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
