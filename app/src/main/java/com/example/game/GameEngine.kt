package com.example.game

import com.example.audio.GameAudioEngine
import com.example.model.GameObject
import com.example.model.Level
import com.example.model.ObjectType
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

enum class PlayerGameMode {
    CUBE,
    SHIP,
    WAVE,
    UFO
}

data class Checkpoint(
    val x: Float,
    val y: Float,
    val vy: Float,
    val gravity: Float,
    val mode: PlayerGameMode,
    val speed: Float,
    val rotation: Float,
    val bgColor: Long,
    val groundColor: Long
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    val colorHex: Long,
    val size: Float,
    val maxLife: Float,
    var life: Float = 0f
)

data class OrbRing(
    var x: Float,
    var y: Float,
    var r: Float
)

class GameEngine(
    val level: Level,
    var isPracticeMode: Boolean = false,
    val onLevelCompleted: (attempts: Int, jumps: Int, coinsCount: Int) -> Unit,
    val onDeath: (percentage: Int) -> Unit
) {
    // Player state
    var playerX: Float = 0f
    var playerY: Float = 0f
    var playerVy: Float = 0f
    var gravityDirection: Float = 1f // 1 = down, -1 = up
    var gameMode: PlayerGameMode = PlayerGameMode.CUBE
    var speedMultiplier: Float = 10.4f // authentic 1x GD speed (blocks/s)
    var playerRotation: Float = 0f
    var isOnGround: Boolean = true
    var isHolding: Boolean = false

    // Real-time Background and Ground colors (modified by Color Triggers!)
    var currentBgColor: Long = level.bgColor
    var currentGroundColor: Long = level.groundColor
    var targetBgColor: Long = level.bgColor
    var targetGroundColor: Long = level.groundColor

    // Game progress
    var isDead: Boolean = false
    var isWon: Boolean = false
    var percentage: Int = 0
    var attemptsCount: Int = 1
    var jumpsCount: Int = 0
    val collectedCoinIds = mutableSetOf<Long>()
    private val activatedTriggerIds = mutableSetOf<Long>()
    private var lastActivatedOrbId: Long = -1L

    // Checkpoints for Practice Mode
    val checkpoints = mutableListOf<Checkpoint>()

    // Visual particles and effects
    val particles = mutableListOf<Particle>()
    val orbRingEffects = mutableListOf<OrbRing>()
    var sawRotation: Float = 0f

    // Pre-sorted objects for spatial queries
    val sortedObjects: List<GameObject> = level.objects.sortedBy { it.x }

    // Level bounds
    val levelLength: Float = level.lengthInGridUnits
    val floorY: Float = 0f
    val ceilingY: Float = 8f

    // Death delay timer
    private var deathTimer: Float = 0f

    // Fixed timestep physics accumulator
    private var physicsAccumulator: Float = 0f

    init {
        resetPlayer(softReset = false)
    }

    fun resetPlayer(softReset: Boolean = false) {
        if (isPracticeMode && checkpoints.isNotEmpty()) {
            val cp = checkpoints.last()
            playerX = cp.x
            playerY = cp.y
            playerVy = cp.vy
            gravityDirection = cp.gravity
            gameMode = cp.mode
            speedMultiplier = cp.speed
            playerRotation = cp.rotation
            currentBgColor = cp.bgColor
            currentGroundColor = cp.groundColor
            targetBgColor = cp.bgColor
            targetGroundColor = cp.groundColor
            isDead = false
            isOnGround = false
            isHolding = false
            deathTimer = 0f
            physicsAccumulator = 0f
            lastActivatedOrbId = -1L
            return
        }

        playerX = 0f
        playerY = 0f
        playerVy = 0f
        gravityDirection = 1f
        gameMode = PlayerGameMode.CUBE
        speedMultiplier = 10.4f
        playerRotation = 0f
        isOnGround = true
        isDead = false
        isHolding = false
        deathTimer = 0f
        physicsAccumulator = 0f
        currentBgColor = level.bgColor
        currentGroundColor = level.groundColor
        targetBgColor = level.bgColor
        targetGroundColor = level.groundColor
        activatedTriggerIds.clear()
        lastActivatedOrbId = -1L

        if (!softReset) {
            collectedCoinIds.clear()
        }
    }

    fun addCheckpoint() {
        if (!isPracticeMode || isDead) return
        checkpoints.add(
            Checkpoint(
                x = playerX,
                y = playerY,
                vy = playerVy,
                gravity = gravityDirection,
                mode = gameMode,
                speed = speedMultiplier,
                rotation = playerRotation,
                bgColor = targetBgColor,
                groundColor = targetGroundColor
            )
        )
        GameAudioEngine.playCheckpoint()
    }

    fun removeLastCheckpoint() {
        if (checkpoints.isNotEmpty()) {
            checkpoints.removeAt(checkpoints.size - 1)
        }
    }

    fun onPointerDown() {
        if (isDead) return
        isHolding = true

        when (gameMode) {
            PlayerGameMode.CUBE -> {
                val tappedOrb = checkOrbTrigger()
                if (tappedOrb != null) {
                    activateOrb(tappedOrb)
                    return
                }

                if (isOnGround) {
                    performJump()
                }
            }
            PlayerGameMode.UFO -> {
                val tappedOrb = checkOrbTrigger()
                if (tappedOrb != null) {
                    activateOrb(tappedOrb)
                    return
                }
                performUfoHop()
            }
            PlayerGameMode.SHIP, PlayerGameMode.WAVE -> {
                // Holding state handles vertical movement
            }
        }
    }

    private fun performUfoHop(force: Float = 16.5f) {
        playerVy = -force * gravityDirection
        isOnGround = false
        jumpsCount++
        GameAudioEngine.playJump()

        // Thruster sparks at UFO base
        for (i in 0..4) {
            if (particles.size < 40) {
                particles.add(
                    Particle(
                        x = playerX + 0.5f + (Random.nextFloat() - 0.5f) * 0.4f,
                        y = if (gravityDirection > 0) playerY else playerY + 0.8f,
                        vx = (Random.nextFloat() - 0.5f) * 3f,
                        vy = (Random.nextFloat() * 4f + 2f) * gravityDirection,
                        alpha = 1f,
                        colorHex = 0xFFFF9100,
                        size = 4f,
                        maxLife = 0.22f
                    )
                )
            }
        }
    }

    fun onPointerUp() {
        isHolding = false
    }

    private fun performJump(force: Float = 19.5f) {
        playerVy = -force * gravityDirection
        isOnGround = false
        jumpsCount++
        GameAudioEngine.playJump()

        // Jump dust particles
        for (i in 0..4) {
            if (particles.size < 40) {
                particles.add(
                    Particle(
                        x = playerX + 0.5f,
                        y = if (gravityDirection > 0) playerY else playerY + 1f,
                        vx = (Random.nextFloat() - 0.5f) * 4f,
                        vy = (Random.nextFloat() * 3f) * gravityDirection,
                        alpha = 1f,
                        colorHex = 0xAAFFFFFF,
                        size = 4f,
                        maxLife = 0.25f
                    )
                )
            }
        }
    }

    private fun checkOrbTrigger(): GameObject? {
        val playerCenterX = playerX + 0.5f
        val playerCenterY = playerY + 0.5f
        val triggerRadius = 1.35f

        for (obj in sortedObjects) {
            if (obj.x < playerX - 2f) continue
            if (obj.x > playerX + 2f) break

            if (obj.type.category == com.example.model.ObjectCategory.ORBS &&
                abs(playerCenterX - (obj.x + 0.5f)) < triggerRadius &&
                abs(playerCenterY - (obj.y + 0.5f)) < triggerRadius
            ) {
                return obj
            }
        }
        return null
    }

    private fun activateOrb(orb: GameObject) {
        lastActivatedOrbId = orb.id
        orbRingEffects.add(OrbRing(orb.x + 0.5f, orb.y + 0.5f, 0.2f))
        when (orb.type) {
            ObjectType.ORB_YELLOW -> {
                playerVy = -19.5f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_PINK -> {
                playerVy = -14f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_BLUE -> {
                gravityDirection = -gravityDirection
                playerVy = -8f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playGravity()
            }
            ObjectType.ORB_GREEN -> {
                gravityDirection = -gravityDirection
                playerVy = -19f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playGravity()
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_RED -> {
                playerVy = -24f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_BLACK -> {
                playerVy = 24f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_DASH -> {
                playerVy = -5f * gravityDirection
                playerX += 2.2f
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_RAINBOW -> {
                playerVy = -21f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            else -> {}
        }
    }

    fun update(rawDt: Float) {
        val dt = rawDt.coerceIn(0.001f, 0.05f)
        updateVisualEffects(dt)

        if (isDead) {
            deathTimer += dt
            if (deathTimer > 0.75f) {
                attemptsCount++
                resetPlayer(softReset = isPracticeMode)
            }
            return
        }

        if (isWon) return

        // Smooth fixed-timestep physics sub-stepping (120 Hz)
        physicsAccumulator += dt
        val fixedDt = 1f / 120f
        var steps = 0
        while (physicsAccumulator >= fixedDt && steps < 4) {
            stepPhysics(fixedDt)
            physicsAccumulator -= fixedDt
            steps++
            if (isDead || isWon) break
        }
        if (physicsAccumulator > fixedDt * 2) {
            physicsAccumulator = 0f
        }
    }

    private fun stepPhysics(dt: Float) {
        // Update progress percentage
        val progress = ((playerX / (levelLength - 10f)) * 100f).toInt().coerceIn(0, 100)
        if (progress > percentage) {
            percentage = progress
        }

        // Win condition
        if (playerX >= levelLength - 5f) {
            isWon = true
            percentage = 100
            GameAudioEngine.playWin()
            onLevelCompleted(attemptsCount, jumpsCount, collectedCoinIds.size)
            return
        }

        // Advance horizontally
        playerX += speedMultiplier * dt

        // Check for buffer orb tap when holding
        if (isHolding && gameMode == PlayerGameMode.CUBE) {
            val orb = checkOrbTrigger()
            if (orb != null && orb.id != lastActivatedOrbId) {
                activateOrb(orb)
            }
        }

        // Physics based on Game Mode
        when (gameMode) {
            PlayerGameMode.CUBE -> updateCubePhysics(dt)
            PlayerGameMode.SHIP -> updateShipPhysics(dt)
            PlayerGameMode.WAVE -> updateWavePhysics(dt)
            PlayerGameMode.UFO -> updateUfoPhysics(dt)
        }

        // Check Object Collisions
        checkObjectCollisions()
    }

    private fun lerpColor(c1: Long, c2: Long, factor: Float): Long {
        val a1 = ((c1 shr 24) and 0xFF).toFloat()
        val r1 = ((c1 shr 16) and 0xFF).toFloat()
        val g1 = ((c1 shr 8) and 0xFF).toFloat()
        val b1 = (c1 and 0xFF).toFloat()

        val a2 = ((c2 shr 24) and 0xFF).toFloat()
        val r2 = ((c2 shr 16) and 0xFF).toFloat()
        val g2 = ((c2 shr 8) and 0xFF).toFloat()
        val b2 = (c2 and 0xFF).toFloat()

        val a = (a1 + (a2 - a1) * factor).toLong().coerceIn(0L, 255L)
        val r = (r1 + (r2 - r1) * factor).toLong().coerceIn(0L, 255L)
        val g = (g1 + (g2 - g1) * factor).toLong().coerceIn(0L, 255L)
        val b = (b1 + (b2 - b1) * factor).toLong().coerceIn(0L, 255L)

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun updateVisualEffects(dt: Float) {
        sawRotation = (sawRotation + 340f * dt) % 360f

        // Smooth background & ground color transition from triggers
        currentBgColor = lerpColor(currentBgColor, targetBgColor, (dt * 6f).coerceAtMost(1f))
        currentGroundColor = lerpColor(currentGroundColor, targetGroundColor, (dt * 6f).coerceAtMost(1f))

        // Trail particles
        if (!isDead && !isWon && Random.nextFloat() < 0.45f && particles.size < 40) {
            particles.add(
                Particle(
                    x = playerX,
                    y = playerY + 0.5f,
                    vx = -2f,
                    vy = (Random.nextFloat() - 0.5f) * 1.5f,
                    alpha = 0.8f,
                    colorHex = when (gameMode) {
                        PlayerGameMode.SHIP -> 0xFFFF4081
                        PlayerGameMode.WAVE -> 0xFF00E5FF
                        PlayerGameMode.UFO -> 0xFFFF9100
                        PlayerGameMode.CUBE -> 0xFFFFE600
                    },
                    size = 5f,
                    maxLife = 0.3f
                )
            )
        }

        // Update particle lifetimes
        val pIt = particles.iterator()
        while (pIt.hasNext()) {
            val p = pIt.next()
            p.life += dt
            if (p.life >= p.maxLife) {
                pIt.remove()
                continue
            }
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
        }

        // Orb ring ripple effects
        val rIt = orbRingEffects.iterator()
        while (rIt.hasNext()) {
            val ring = rIt.next()
            ring.r += dt * 4f
            if (ring.r >= 1.8f) {
                rIt.remove()
            }
        }
    }

    private fun updateCubePhysics(dt: Float) {
        // Authentic GD Cube Gravity
        val gravity = 52f * gravityDirection
        playerVy += gravity * dt

        // Continuous buffer jump on hold
        if (isHolding && isOnGround) {
            performJump()
        }

        playerY -= playerVy * dt

        // Floor / Ceiling check
        if (gravityDirection > 0) {
            if (playerY <= floorY) {
                playerY = floorY
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else {
                isOnGround = false
                playerRotation += 420f * dt
            }
        } else {
            // Upside down floor
            if (playerY >= ceilingY - 1f) {
                playerY = ceilingY - 1f
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else {
                isOnGround = false
                playerRotation += 420f * dt
            }
        }
    }

    private fun updateShipPhysics(dt: Float) {
        // Authentic GD Ship Flight Physics
        val shipAccel = 24f
        val shipGravity = 20f

        if (isHolding) {
            playerVy -= shipAccel * gravityDirection * dt
        } else {
            playerVy += shipGravity * gravityDirection * dt
        }
        playerVy = playerVy.coerceIn(-9.8f, 9.8f)
        playerY -= playerVy * dt

        // Pitch rotation based on velocity
        val targetRot = (-playerVy * 3.8f * gravityDirection).coerceIn(-35f, 35f)
        playerRotation += (targetRot - playerRotation) * 0.22f

        // Ship floor and ceiling death
        if (playerY < floorY) {
            playerY = floorY
            killPlayer()
        }
        if (playerY > ceilingY - 1f) {
            playerY = ceilingY - 1f
            killPlayer()
        }
    }

    private fun updateWavePhysics(dt: Float) {
        // Authentic Geometry Dash Wave:
        // Constant 45-degree angle.
        // Holding -> flies UP at 45 degrees
        // Releasing -> flies DOWN at 45 degrees
        val vertSpeed = speedMultiplier
        if (isHolding) {
            playerVy = -vertSpeed * gravityDirection
            playerRotation = -45f * gravityDirection
        } else {
            playerVy = vertSpeed * gravityDirection
            playerRotation = 45f * gravityDirection
        }
        playerY -= playerVy * dt

        // Touching floor or ceiling kills the Wave!
        if (playerY <= floorY) {
            playerY = floorY
            killPlayer()
        }
        if (playerY >= ceilingY - 1f) {
            playerY = ceilingY - 1f
            killPlayer()
        }

        // Wave trail spark particles
        if (particles.size < 40 && Random.nextFloat() < 0.65f) {
            particles.add(
                Particle(
                    x = playerX + 0.2f,
                    y = playerY + 0.5f,
                    vx = -speedMultiplier * 0.35f,
                    vy = (Random.nextFloat() - 0.5f) * 1.5f,
                    alpha = 0.9f,
                    colorHex = 0xFF00E5FF,
                    size = 3.5f,
                    maxLife = 0.2f
                )
            )
        }
    }

    private fun updateUfoPhysics(dt: Float) {
        val ufoGravity = 46f * gravityDirection
        playerVy += ufoGravity * dt
        playerVy = playerVy.coerceIn(-17f, 17f)
        playerY -= playerVy * dt

        // Subtle pitch tilt while ascending or falling
        val targetRot = (-playerVy * 1.8f * gravityDirection).coerceIn(-25f, 25f)
        playerRotation += (targetRot - playerRotation) * 0.25f

        // Ceiling is lethal to UFO
        if (gravityDirection > 0 && playerY >= ceilingY - 1f) {
            playerY = ceilingY - 1f
            killPlayer()
        } else if (gravityDirection < 0 && playerY <= floorY) {
            playerY = floorY
            killPlayer()
        }

        // Floor landing / bouncing
        if (gravityDirection > 0) {
            if (playerY <= floorY) {
                playerY = floorY
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else {
                isOnGround = false
            }
        } else {
            if (playerY >= ceilingY - 1f) {
                playerY = ceilingY - 1f
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else {
                isOnGround = false
            }
        }
    }

    private fun snapRotation() {
        val deg = (playerRotation % 360f + 360f) % 360f
        val snapped = (deg / 90f).roundToInt() * 90f
        playerRotation = snapped
    }

    private fun checkObjectCollisions() {
        // Realistic Geometry Dash player hitboxes
        val pLeft = playerX + 0.22f
        val pRight = playerX + 0.78f
        val pBottom = playerY + 0.12f
        val pTop = playerY + 0.88f

        for (obj in sortedObjects) {
            if (obj.x < playerX - 3f) continue
            if (obj.x > playerX + 3f) break

            val oLeft = obj.x
            val oRight = obj.x + obj.type.width
            val oBottom = obj.y
            val oTop = obj.y + obj.type.height

            // 1. Color Triggers: trigger when player crosses trigger X
            if (obj.type.category == com.example.model.ObjectCategory.TRIGGERS) {
                if (playerX >= obj.x - 0.2f && !activatedTriggerIds.contains(obj.id)) {
                    activatedTriggerIds.add(obj.id)
                    val targetColor = obj.customColorHex ?: obj.type.primaryColorHex
                    if (obj.type.name.startsWith("TRIGGER_BG_")) {
                        targetBgColor = targetColor
                    } else if (obj.type.name.startsWith("TRIGGER_GROUND_")) {
                        targetGroundColor = targetColor
                    }
                }
                continue
            }

            // AABB broadphase check
            val isOverlap = pRight > oLeft && pLeft < oRight && pTop > oBottom && pBottom < oTop
            if (!isOverlap) continue

            when {
                // Circular Sawblades
                obj.type in listOf(
                    ObjectType.SAWBLADE_GIANT,
                    ObjectType.SAWBLADE_LARGE,
                    ObjectType.SAWBLADE_MEDIUM,
                    ObjectType.SAWBLADE_SMALL
                ) -> {
                    val sCenterX = obj.x + obj.type.width * 0.5f
                    val sCenterY = obj.y + obj.type.height * 0.5f
                    val pCenterX = playerX + 0.5f
                    val pCenterY = playerY + 0.5f
                    val dx = pCenterX - sCenterX
                    val dy = pCenterY - sCenterY
                    val distSq = (dx * dx) + (dy * dy)
                    // Inner lethal radius with authentic margin
                    val lethalRadius = (obj.type.width * 0.38f) + 0.18f
                    if (distSq < lethalRadius * lethalRadius) {
                        killPlayer()
                        return
                    }
                }

                // Spikes: Authentic Geometry Dash triangle hitbox check
                obj.type.isLethal -> {
                    val isHanging = (obj.type == ObjectType.SPIKE_HANGING)
                    val sHeight = obj.type.height
                    val sLeft = oLeft + 0.20f
                    val sRight = oRight - 0.20f
                    val sBottom = oBottom + 0.06f
                    val sTop = oTop - 0.06f

                    if (pRight > sLeft && pLeft < sRight && pTop > sBottom && pBottom < sTop) {
                        // Fine triangle slope check: prevents dying on empty air next to spike tip!
                        val numSpikes = obj.type.width.toInt().coerceAtLeast(1)
                        var hitActualSpike = false
                        val px = (pLeft + pRight) * 0.5f
                        val py = (pBottom + pTop) * 0.5f

                        for (sIdx in 0 until numSpikes) {
                            val subCenterX = oLeft + sIdx + 0.5f
                            val subHalfWidth = 0.38f
                            if (isHanging) {
                                // Hanging upside down: apex is at bottom
                                val relY = ((sTop - py) / sHeight).coerceIn(0f, 1f)
                                val maxDx = subHalfWidth * (1f - (relY * 0.7f))
                                if (abs(px - subCenterX) < maxDx) {
                                    hitActualSpike = true
                                    break
                                }
                            } else {
                                // Ground spike: apex is at top
                                val relY = ((py - sBottom) / sHeight).coerceIn(0f, 1f)
                                val maxDx = subHalfWidth * (1f - (relY * 0.7f))
                                if (abs(px - subCenterX) < maxDx) {
                                    hitActualSpike = true
                                    break
                                }
                            }
                        }

                        if (hitActualSpike) {
                            killPlayer()
                            return
                        }
                    }
                }

                obj.type.isSolid -> {
                    handleBlockCollision(obj)
                }

                obj.type.category == com.example.model.ObjectCategory.PADS -> {
                    handlePadTrigger(obj)
                }

                obj.type.category == com.example.model.ObjectCategory.PORTALS -> {
                    handlePortalTrigger(obj)
                }

                obj.type == ObjectType.COIN -> {
                    if (!collectedCoinIds.contains(obj.id)) {
                        collectedCoinIds.add(obj.id)
                        GameAudioEngine.playCoin()
                        // Coin sparkle effect
                        for (i in 0..8) {
                            if (particles.size < 40) {
                                particles.add(
                                    Particle(
                                        x = obj.x + 0.5f,
                                        y = obj.y + 0.5f,
                                        vx = (Random.nextFloat() - 0.5f) * 6f,
                                        vy = (Random.nextFloat() - 0.5f) * 6f,
                                        alpha = 1f,
                                        colorHex = 0xFFFFD700,
                                        size = 6f,
                                        maxLife = 0.45f
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleBlockCollision(obj: GameObject) {
        if (gameMode == PlayerGameMode.WAVE) {
            // Wave crashes instantly on any solid block
            killPlayer()
            return
        }

        val oTop = obj.y + obj.type.height
        val oBottom = obj.y
        val oLeft = obj.x

        if (gravityDirection > 0) {
            // Normal Gravity: Landing on top of block
            if (playerY >= oTop - 0.28f && playerVy >= 0f) {
                playerY = oTop
                playerVy = 0f
                isOnGround = true
                snapRotation()
                if (isHolding && gameMode == PlayerGameMode.CUBE) {
                    performJump()
                }
            } else if (playerX + 0.78f > oLeft && playerX + 0.15f < oLeft && playerY < oTop - 0.18f && playerY + 0.85f > oBottom) {
                // Crashed into side wall of block!
                killPlayer()
            }
        } else {
            // Inverted gravity: landing on bottom of ceiling block
            if (playerY + 1f <= oBottom + 0.28f && playerVy <= 0f) {
                playerY = oBottom - 1f
                playerVy = 0f
                isOnGround = true
                snapRotation()
                if (isHolding && gameMode == PlayerGameMode.CUBE) {
                    performJump()
                }
            } else if (playerX + 0.78f > oLeft && playerX + 0.15f < oLeft && playerY + 0.82f > oBottom + 0.18f && playerY + 0.15f < oTop) {
                killPlayer()
            }
        }
    }

    private fun handlePadTrigger(pad: GameObject) {
        when (pad.type) {
            ObjectType.PAD_YELLOW -> {
                playerVy = -22f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playPad()
            }
            ObjectType.PAD_PINK -> {
                playerVy = -15f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playPad()
            }
            ObjectType.PAD_RED -> {
                playerVy = -27f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playPad()
            }
            ObjectType.PAD_PURPLE -> {
                playerVy = -12f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playPad()
            }
            ObjectType.PAD_GRAVITY -> {
                gravityDirection = -gravityDirection
                playerVy = -9f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playGravity()
                GameAudioEngine.playPad()
            }
            else -> {}
        }
    }

    private fun handlePortalTrigger(portal: GameObject) {
        when (portal.type) {
            ObjectType.PORTAL_SHIP -> {
                if (gameMode != PlayerGameMode.SHIP) {
                    gameMode = PlayerGameMode.SHIP
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_CUBE -> {
                if (gameMode != PlayerGameMode.CUBE) {
                    gameMode = PlayerGameMode.CUBE
                    snapRotation()
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_WAVE -> {
                if (gameMode != PlayerGameMode.WAVE) {
                    gameMode = PlayerGameMode.WAVE
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_UFO -> {
                if (gameMode != PlayerGameMode.UFO) {
                    gameMode = PlayerGameMode.UFO
                    snapRotation()
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_GRAVITY_INVERT -> {
                if (gravityDirection > 0) {
                    gravityDirection = -1f
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_GRAVITY_NORMAL -> {
                if (gravityDirection < 0) {
                    gravityDirection = 1f
                    GameAudioEngine.playGravity()
                }
            }
            ObjectType.PORTAL_SPEED_0_5X -> speedMultiplier = 8f
            ObjectType.PORTAL_SPEED_1X -> speedMultiplier = 10.4f
            ObjectType.PORTAL_SPEED_2X -> speedMultiplier = 13.8f
            ObjectType.PORTAL_SPEED_3X -> speedMultiplier = 17.2f
            ObjectType.PORTAL_SPEED_4X -> speedMultiplier = 20.8f
            else -> {}
        }
    }

    private fun killPlayer() {
        if (isDead) return
        isDead = true
        deathTimer = 0f
        GameAudioEngine.playDeath()
        onDeath(percentage)

        // Spectacular death explosion particles
        particles.clear()
        for (i in 0..18) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 10f + 2f
            particles.add(
                Particle(
                    x = playerX + 0.5f,
                    y = playerY + 0.5f,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    alpha = 1f,
                    colorHex = if (i % 2 == 0) 0xFFFF1744 else 0xFFFFEA00,
                    size = Random.nextFloat() * 6f + 3f,
                    maxLife = 0.5f
                )
            )
        }
    }
}
