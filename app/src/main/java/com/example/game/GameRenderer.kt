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
        for (obj in engine.sortedObjects) {
            if (obj.x > maxX) break
            if (obj.x + obj.type.width < minX) continue
            drawGameObject(scope, obj, cameraX, groundScreenY, tileSize)
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
        val baseBg = Color(engine.level.bgColor)
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
        val groundBaseColor = Color(engine.level.groundColor)
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

    private fun drawGameObject(
        scope: DrawScope,
        obj: GameObject,
        cameraX: Float,
        groundScreenY: Float,
        tileSize: Float
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

            ObjectType.PAD_YELLOW, ObjectType.PAD_PINK, ObjectType.PAD_GRAVITY -> {
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

            ObjectType.ORB_YELLOW, ObjectType.ORB_PINK, ObjectType.ORB_BLUE, ObjectType.ORB_GREEN -> {
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
                    color = orbColor.copy(alpha = 0.8f),
                    radius = radius * 0.5f,
                    center = center
                )
                scope.drawCircle(
                    color = Color.White,
                    radius = radius * 0.25f,
                    center = center
                )
            }

            ObjectType.PORTAL_SHIP, ObjectType.PORTAL_CUBE,
            ObjectType.PORTAL_GRAVITY_INVERT, ObjectType.PORTAL_GRAVITY_NORMAL,
            ObjectType.PORTAL_SPEED_0_5X, ObjectType.PORTAL_SPEED_1X, ObjectType.PORTAL_SPEED_2X -> {
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
