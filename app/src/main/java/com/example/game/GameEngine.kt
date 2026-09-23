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
    SHIP
}

data class Checkpoint(
    val x: Float,
    val y: Float,
    val vy: Float,
    val gravity: Float,
    val mode: PlayerGameMode,
    val speed: Float,
    val rotation: Float
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
    var speedMultiplier: Float = 10.5f // units per second
    var playerRotation: Float = 0f
    var isOnGround: Boolean = true
    var isHolding: Boolean = false

    // Game progress
    var isDead: Boolean = false
    var isWon: Boolean = false
    var percentage: Int = 0
    var attemptsCount: Int = 1
    var jumpsCount: Int = 0
    val collectedCoinIds = mutableSetOf<Long>()

    // Checkpoints for Practice Mode
    val checkpoints = mutableListOf<Checkpoint>()

    // Visual particles
    val particles = mutableListOf<Particle>()
    val orbRingEffects = mutableListOf<Triple<Float, Float, Float>>() // x, y, radius

    // Level bounds
    val levelLength: Float = level.lengthInGridUnits
    val floorY: Float = 0f
    val ceilingY: Float = 8f

    // Death delay timer
    private var deathTimer: Float = 0f

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
            isDead = false
            isOnGround = false
            isHolding = false
            deathTimer = 0f
            return
        }

        playerX = 0f
        playerY = 0f
        playerVy = 0f
        gravityDirection = 1f
        gameMode = PlayerGameMode.CUBE
        speedMultiplier = 10.5f
        playerRotation = 0f
        isOnGround = true
        isDead = false
        isHolding = false
        deathTimer = 0f

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
                rotation = playerRotation
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

        if (gameMode == PlayerGameMode.CUBE) {
            // Check for tap inside jump orb radius first!
            val tappedOrb = checkOrbTrigger()
            if (tappedOrb != null) {
                activateOrb(tappedOrb)
                return
            }

            // Normal jump if on ground
            if (isOnGround) {
                performJump()
            }
        }
    }

    fun onPointerUp() {
        isHolding = false
    }

    private fun performJump(force: Float = 17.5f) {
        playerVy = -force * gravityDirection
        isOnGround = false
        jumpsCount++
        GameAudioEngine.playJump()

        // Jump dust particles
        for (i in 0..5) {
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

    private fun checkOrbTrigger(): GameObject? {
        val playerCenterX = playerX + 0.5f
        val playerCenterY = playerY + 0.5f
        val triggerRadius = 1.35f

        return level.objects.firstOrNull { obj ->
            obj.type.category == com.example.model.ObjectCategory.ORBS &&
                    abs(playerCenterX - (obj.x + 0.5f)) < triggerRadius &&
                    abs(playerCenterY - (obj.y + 0.5f)) < triggerRadius
        }
    }

    private fun activateOrb(orb: GameObject) {
        orbRingEffects.add(Triple(orb.x + 0.5f, orb.y + 0.5f, 0.2f))
        when (orb.type) {
            ObjectType.ORB_YELLOW -> {
                playerVy = -18f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playOrb()
            }
            ObjectType.ORB_PINK -> {
                playerVy = -13f * gravityDirection
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
                playerVy = -17f * gravityDirection
                isOnGround = false
                jumpsCount++
                GameAudioEngine.playGravity()
                GameAudioEngine.playOrb()
            }
            else -> {}
        }
    }

    fun update(dt: Float) {
        updateParticles(dt)

        if (isDead) {
            deathTimer += dt
            if (deathTimer > 0.75f) {
                attemptsCount++
                resetPlayer(softReset = isPracticeMode)
            }
            return
        }

        if (isWon) return

        // Update progress percentage
        val progress = ((playerX / (levelLength - 10f)) * 100f).toInt().coerceIn(0, 100)
        if (progress > percentage) {
            percentage = progress
        }

        // Win condition: reached end of level!
        if (playerX >= levelLength - 5f) {
            isWon = true
            percentage = 100
            GameAudioEngine.playWin()
            onLevelCompleted(attemptsCount, jumpsCount, collectedCoinIds.size)
            return
        }

        // Advance horizontally
        playerX += speedMultiplier * dt

        // Trail particles
        if (Random.nextFloat() < 0.6f) {
            particles.add(
                Particle(
                    x = playerX,
                    y = playerY + 0.5f,
                    vx = -2f,
                    vy = (Random.nextFloat() - 0.5f) * 1.5f,
                    alpha = 0.8f,
                    colorHex = if (gameMode == PlayerGameMode.SHIP) 0xFFFF4081 else 0xFF00E5FF,
                    size = 5f,
                    maxLife = 0.3f
                )
            )
        }

        // Physics based on Game Mode
        if (gameMode == PlayerGameMode.CUBE) {
            updateCubePhysics(dt)
        } else {
            updateShipPhysics(dt)
        }

        // Check Object Collisions
        checkObjectCollisions()
    }

    private fun updateCubePhysics(dt: Float) {
        val gravity = 46f * gravityDirection
        playerVy += gravity * dt

        // Continuous jump on hold
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
            // Upside down floor is at ceilingY
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
        val shipAccel = 28f
        val shipGravity = 22f

        if (isHolding) {
            playerVy -= shipAccel * gravityDirection * dt
        } else {
            playerVy += shipGravity * gravityDirection * dt
        }
        playerVy = playerVy.coerceIn(-12f, 12f)
        playerY -= playerVy * dt

        // Pitch rotation based on velocity
        val targetRot = (-playerVy * 3.5f * gravityDirection).coerceIn(-40f, 40f)
        playerRotation += (targetRot - playerRotation) * 0.2f

        // Ceiling / Floor bounds for ship
        if (playerY < floorY) {
            playerY = floorY
            killPlayer()
        }
        if (playerY > ceilingY - 1f) {
            playerY = ceilingY - 1f
            killPlayer()
        }
    }

    private fun snapRotation() {
        // Snap rotation to nearest 90 degrees on landing
        val deg = (playerRotation % 360f + 360f) % 360f
        val snapped = (deg / 90f).roundToInt() * 90f
        playerRotation = snapped
    }

    private fun checkObjectCollisions() {
        val pLeft = playerX + 0.1f
        val pRight = playerX + 0.9f
        val pBottom = playerY + 0.05f
        val pTop = playerY + 0.95f

        for (obj in level.objects) {
            // Only check objects within active window
            if (obj.x < playerX - 2f || obj.x > playerX + 2f) continue

            val oLeft = obj.x
            val oRight = obj.x + obj.type.width
            val oBottom = obj.y
            val oTop = obj.y + obj.type.height

            // Check AABB overlap
            val isOverlap = pRight > oLeft && pLeft < oRight && pTop > oBottom && pBottom < oTop

            if (!isOverlap) continue

            when {
                obj.type.isLethal -> {
                    // Geometry Dash fairness: tighter hitbox for spikes
                    val insetX = 0.22f
                    val insetY = 0.18f
                    val sLeft = oLeft + insetX
                    val sRight = oRight - insetX
                    val sBottom = oBottom + insetY
                    val sTop = oTop - insetY

                    if (pRight > sLeft && pLeft < sRight && pTop > sBottom && pBottom < sTop) {
                        killPlayer()
                        return
                    }
                }

                obj.type.isSolid -> {
                    // Solid block collision
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
                        for (i in 0..12) {
                            particles.add(
                                Particle(
                                    x = obj.x + 0.5f,
                                    y = obj.y + 0.5f,
                                    vx = (Random.nextFloat() - 0.5f) * 6f,
                                    vy = (Random.nextFloat() - 0.5f) * 6f,
                                    alpha = 1f,
                                    colorHex = 0xFFFFD700,
                                    size = 6f,
                                    maxLife = 0.5f
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleBlockCollision(obj: GameObject) {
        val oTop = obj.y + obj.type.height
        val oBottom = obj.y
        val oLeft = obj.x

        if (gravityDirection > 0) {
            // Landing on top of block
            if (playerY >= oTop - 0.25f && playerVy >= 0) {
                playerY = oTop
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else if (playerX + 0.85f > oLeft && playerY < oTop - 0.1f) {
                // Crashed into side of block!
                killPlayer()
            }
        } else {
            // Inverted gravity: landing on bottom of ceiling block
            if (playerY + 1f <= oBottom + 0.25f && playerVy <= 0) {
                playerY = oBottom - 1f
                playerVy = 0f
                isOnGround = true
                snapRotation()
            } else if (playerX + 0.85f > oLeft && playerY + 1f > oBottom + 0.1f) {
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
            ObjectType.PAD_GRAVITY -> {
                gravityDirection = -gravityDirection
                playerVy = -10f * gravityDirection
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
            ObjectType.PORTAL_SPEED_1X -> speedMultiplier = 10.5f
            ObjectType.PORTAL_SPEED_2X -> speedMultiplier = 14f
            else -> {}
        }
    }

    private fun killPlayer() {
        if (isDead) return
        isDead = true
        deathTimer = 0f
        GameAudioEngine.playDeath()
        onDeath(percentage)

        // Spectacular death explosion particles!
        for (i in 0..25) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 12f + 3f
            particles.add(
                Particle(
                    x = playerX + 0.5f,
                    y = playerY + 0.5f,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    alpha = 1f,
                    colorHex = if (i % 2 == 0) 0xFFFF1744 else 0xFFFFEA00,
                    size = Random.nextFloat() * 8f + 4f,
                    maxLife = 0.6f
                )
            )
        }
    }

    private fun updateParticles(dt: Float) {
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

        val rIt = orbRingEffects.iterator()
        val updatedRings = mutableListOf<Triple<Float, Float, Float>>()
        while (rIt.hasNext()) {
            val (rx, ry, r) = rIt.next()
            val newR = r + dt * 4f
            if (newR < 1.8f) {
                updatedRings.add(Triple(rx, ry, newR))
            }
        }
        orbRingEffects.clear()
        orbRingEffects.addAll(updatedRings)
    }
}
