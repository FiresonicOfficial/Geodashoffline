package com.example.data

import com.example.model.Difficulty
import com.example.model.GameObject
import com.example.model.Level
import com.example.model.ObjectType

object DefaultLevels {

    fun getAllDefaultLevels(): List<Level> {
        return listOf(
            createStereoMadness(),
            createBackOnTrack(),
            createPolargeist(),
            createDryOut(),
            createBaseAfterBase(),
            createCantLetGo(),
            createJumper(),
            createTimeMachine(),
            createCycles(),
            createXStep(),
            createClutterfunk(),
            createTheoryOfEverything(),
            createElectroman(),
            createClubstep(),
            createElectrodynamix(),
            createHexagonForce(),
            createBlastProcessing(),
            createDeadlocked()
        )
    }

    // Helper to generate a rhythmic, fair, accessible sequence
    private class LevelBuilder(var cursorX: Float = 10f) {
        val objects = mutableListOf<GameObject>()

        fun add(type: ObjectType, y: Float = 0f, advanceAfter: Float = 0f) {
            objects.add(GameObject(x = cursorX, y = y, type = type))
            if (advanceAfter > 0f) {
                cursorX += advanceAfter
            }
        }

        fun advance(dx: Float) {
            cursorX += dx
        }

        fun addPlatform(length: Int, y: Float, stepWidth: Float = 1.2f, hasGroundHazard: Boolean = false) {
            for (i in 0 until length) {
                val px = cursorX + (i * stepWidth)
                objects.add(GameObject(x = px, y = y, type = ObjectType.BLOCK))
                if (hasGroundHazard && i % 2 == 0) {
                    objects.add(GameObject(x = px, y = 0f, type = ObjectType.SPIKE_SMALL))
                }
            }
            cursorX += length * stepWidth + 2f
        }

        fun addJumpPadOverHazard(padType: ObjectType, hazardType: ObjectType, padY: Float = 0f) {
            objects.add(GameObject(x = cursorX, y = padY, type = padType))
            objects.add(GameObject(x = cursorX + 2.2f, y = 0f, type = hazardType))
            cursorX += 7.5f
        }

        fun addOrbSequence(orbType: ObjectType, orbY: Float = 1.6f, groundSpike: Boolean = true) {
            if (groundSpike) {
                objects.add(GameObject(x = cursorX + 0.8f, y = 0f, type = ObjectType.SPIKE))
            }
            objects.add(GameObject(x = cursorX + 0.8f, y = orbY, type = orbType))
            cursorX += 6.5f
        }

        fun addShipSection(lengthUnits: Float, clearance: Float = 4.2f, hasObstacles: Boolean = true) {
            // Ship Portal
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            cursorX += 5f

            val startX = cursorX
            var currentY = 2.5f

            while (cursorX < startX + lengthUnits) {
                // Ceiling and floor boundaries
                objects.add(GameObject(x = cursorX, y = 7.2f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (hasObstacles && ((cursorX - startX).toInt() % 16 == 0)) {
                    // Gentle floating obstacle
                    objects.add(GameObject(x = cursorX, y = currentY, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 1.2f, y = currentY, type = ObjectType.BLOCK_GRID))
                    currentY = if (currentY > 3.5f) 2.2f else 4.2f
                }
                cursorX += 2.5f
            }

            // Return to Cube Portal
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }
    }

    // 1. STEREO MADNESS (Easy 1★) - 245 units long (~23.5s)
    private fun createStereoMadness(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Stage 1: Basic timing & single spikes (Accessible warm-up)
        for (i in 0..2) {
            b.add(ObjectType.SPIKE, 0f, advanceAfter = 7.5f)
        }

        // Stepped pyramid with Coin 1
        b.add(ObjectType.BLOCK, 0f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 2f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 1.4f) // Coin 1
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 0f, advanceAfter = 6f)

        // Jump pad over double spikes
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Bridge platforms
        b.addPlatform(length = 4, y = 1.5f, hasGroundHazard = true)

        // Background Color Trigger: Shift to vibrant Cyan!
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Yellow jump orbs in air
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)

        // Floating pillar with Coin 2
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.5f)
        b.add(ObjectType.BLOCK, 3f)
        b.add(ObjectType.COIN, 4.5f, advanceAfter = 4f) // Coin 2
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 5f)

        // Ship Flight Sequence (Wide corridor, forgiving navigation)
        b.addShipSection(lengthUnits = 45f, clearance = 4.5f, hasObstacles = false)

        // Background Color Trigger: Shift to deep purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 4f)
        b.add(ObjectType.TRIGGER_GROUND_PURPLE, 0.5f)

        // Cube Sprint: Pads & dual jumps
        for (i in 0..2) {
            b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        }

        // Elevated sprint with Coin 3
        b.addPlatform(length = 5, y = 2f, hasGroundHazard = true)
        b.add(ObjectType.COIN, 3.8f) // Coin 3
        b.advance(4f)

        // Final gentle sprint to finish line (~245m)
        b.add(ObjectType.SPIKE_SMALL, 0f, advanceAfter = 8f)
        b.add(ObjectType.BLOCK_RAINBOW, 0f, advanceAfter = 2f)
        b.add(ObjectType.BLOCK_RAINBOW, 1f, advanceAfter = 2f)
        b.add(ObjectType.BLOCK_RAINBOW, 2f, advanceAfter = 8f)
        b.advance(15f)

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The classic original journey. Over 20s of rhythmic timing, blocks, pads, and ship flight.",
            difficulty = Difficulty.EASY,
            stars = 1,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF0D1B2A,
            groundColor = 0xFF1B263B,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1000L
        )
    }

    // 2. BACK ON TRACK (Easy 2★) - 250 units long (~24s)
    private fun createBackOnTrack(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Bouncy yellow pads
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Elevated bounce sequences with Coin 1
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.4f)
        b.add(ObjectType.BLOCK, 2f)
        b.add(ObjectType.COIN, 3.6f, advanceAfter = 1.2f) // Coin 1
        b.add(ObjectType.PAD_YELLOW, 2f, advanceAfter = 5.5f)

        // Color Trigger: Shift to Ocean Blue
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)

        // Double spike leaps
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)

        // Pink pads (low jump) with Coin 2
        b.add(ObjectType.PAD_PINK, 0f, advanceAfter = 2.2f)
        b.add(ObjectType.BLOCK, 1.2f)
        b.add(ObjectType.COIN, 2.6f, advanceAfter = 3.5f) // Coin 2
        b.add(ObjectType.SPIKE, 0f, advanceAfter = 6f)

        // Sawblade introduction
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.4f)
        b.add(ObjectType.SAWBLADE_SMALL, 1f, advanceAfter = 4f)
        b.add(ObjectType.BLOCK, 0f, advanceAfter = 6f)

        // Ship sequence
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 2f)
        b.addShipSection(lengthUnits = 50f, clearance = 4f, hasObstacles = true)

        // Final pad flurry with Coin 3
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.5f)
        b.add(ObjectType.COIN, 4f) // Coin 3
        b.add(ObjectType.BLOCK, 2.5f, advanceAfter = 5f)
        b.addPlatform(length = 6, y = 1.5f, hasGroundHazard = true)
        b.advance(18f)

        return Level(
            id = "main_back_on_track",
            name = "Back On Track",
            description = "Bouncy rhythm mechanics! Yellow and pink jump pads over spike pits with full length.",
            difficulty = Difficulty.EASY,
            stars = 2,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF003049,
            groundColor = 0xFF023E8A,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1100L
        )
    }

    // 3. POLARGEIST (Normal 3★) - 250 units long (~24s)
    private fun createPolargeist(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Mid-air yellow orbs
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f)

        // Multi-ring chain with Coin 1
        b.add(ObjectType.ORB_YELLOW, 1.6f, advanceAfter = 2.2f)
        b.add(ObjectType.ORB_YELLOW, 2.4f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.add(ObjectType.SPIKE_SMALL, 0f, advanceAfter = 6f)

        // Color trigger
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Platforming with small saw hazards
        b.addPlatform(length = 5, y = 1.5f, hasGroundHazard = true)
        b.add(ObjectType.SAWBLADE_SMALL, 1.8f, advanceAfter = 6f)

        // Pink orb micro-jumps with Coin 2
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.5f)
        b.add(ObjectType.COIN, 2.8f) // Coin 2
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.5f)

        // Smooth ship section
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 2f)
        b.addShipSection(lengthUnits = 55f, clearance = 4.2f, hasObstacles = true)

        // Climax orb and pad rhythm
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.add(ObjectType.COIN, 3.5f) // Coin 3
        b.addPlatform(length = 6, y = 1.5f)
        b.advance(18f)

        return Level(
            id = "main_polargeist",
            name = "Polargeist",
            description = "Mid-air rhythm! Master the yellow and pink jump rings in suspension.",
            difficulty = Difficulty.NORMAL,
            stars = 3,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF14213D,
            groundColor = 0xFF001B2E,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1200L
        )
    }

    // 4. DRY OUT (Normal 3★) - 255 units long (~24.5s)
    private fun createDryOut(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Step pyramids
        b.addPlatform(length = 4, y = 1f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)

        // Color Trigger: Desert Red
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Inverted gravity portal!
        b.add(ObjectType.PORTAL_GRAVITY_INVERT, 1.5f, advanceAfter = 5f)

        // Upside down ceiling platforming with hanging spikes & Coin 1
        for (i in 0..5) {
            b.add(ObjectType.BLOCK_DARK, 6.5f)
            b.add(ObjectType.SPIKE_HANGING, 5.5f)
            b.advance(1.8f)
        }
        b.add(ObjectType.COIN, 4.5f) // Coin 1
        b.advance(4f)

        // Normal gravity return
        b.add(ObjectType.PORTAL_GRAVITY_NORMAL, 5.5f, advanceAfter = 6f)

        // Rotating sawblade hop
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SAWBLADE_MEDIUM)

        // Ship sequence
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 55f, clearance = 4f, hasObstacles = true)

        // Final upside-down flip with Coin 2 & 3
        b.add(ObjectType.PORTAL_GRAVITY_INVERT, 1.5f, advanceAfter = 4f)
        b.add(ObjectType.COIN, 5f) // Coin 2
        b.advance(10f)
        b.add(ObjectType.PORTAL_GRAVITY_NORMAL, 5.5f, advanceAfter = 5f)
        b.add(ObjectType.COIN, 2.5f) // Coin 3
        b.addPlatform(length = 6, y = 1.2f)
        b.advance(18f)

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Upside-down gravity flips! Experience inverted flight and roof platforming.",
            difficulty = Difficulty.NORMAL,
            stars = 3,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF2B0900,
            groundColor = 0xFF3D1300,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1300L
        )
    }

    // 5. BASE AFTER BASE (Normal 4★) - 260 units long (~25s)
    private fun createBaseAfterBase(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 4, y = 1.5f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.add(ObjectType.COIN, 3.5f) // Coin 1
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 4f)

        b.addShipSection(lengthUnits = 60f, clearance = 4.2f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.8f) // Coin 2
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)

        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.6f)
        b.add(ObjectType.COIN, 3.2f) // Coin 3
        b.addPlatform(length = 7, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "Dark atmosphere, rhythm jumps, and balanced ship control across 260 meters.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF0A0F1A,
            groundColor = 0xFF141E30,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1400L
        )
    }

    // 6. CANT LET GO (Normal 4★) - 260 units long (~25s)
    private fun createCantLetGo(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 5, y = 1.2f, hasGroundHazard = true)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_PURPLE, ObjectType.SPIKE)
        b.add(ObjectType.COIN, 3.5f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 4f)
        b.addShipSection(lengthUnits = 60f, clearance = 4f, hasObstacles = true)
        b.add(ObjectType.COIN, 4f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.add(ObjectType.COIN, 3.5f) // Coin 3
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_cant_let_go",
            name = "Can't Let Go",
            description = "Tight gravity flips and hanging spikes test your rhythm and nerve.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF1A0A10,
            groundColor = 0xFF2A0D18,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1500L
        )
    }

    // 7. JUMPER (Hard 5★) - 265 units long (~25.5s)
    private fun createJumper(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.add(ObjectType.COIN, 3.8f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 4f)
        b.addShipSection(lengthUnits = 65f, clearance = 4.2f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.5f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)
        b.addJumpPadOverHazard(ObjectType.PAD_RED, ObjectType.SPIKE_TRIPLE)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.2f)
        b.add(ObjectType.COIN, 4f) // Coin 3
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_jumper",
            name = "Jumper",
            description = "High bouncing energetic pads! Experience vibrant color triggers and giant leaps.",
            difficulty = Difficulty.HARD,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF0D2818,
            groundColor = 0xFF04471C,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1600L
        )
    }

    // 8. TIME MACHINE (Hard 5★) - 265 units long (~25.5s)
    private fun createTimeMachine(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 5, y = 1.2f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 3.5f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 60f, clearance = 4f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.8f) // Coin 2

        b.add(ObjectType.PORTAL_SPEED_1X, 1f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 2f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.add(ObjectType.COIN, 3.5f) // Coin 3
        b.addPlatform(length = 7, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_time_machine",
            name = "Time Machine",
            description = "Speed portals and triple-spike jumps across time and neon dimensions.",
            difficulty = Difficulty.HARD,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF1B0A2A,
            groundColor = 0xFF2E114D,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1700L
        )
    }

    // 9. CYCLES (Hard 6★) - 270 units long (~26s)
    private fun createCycles(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 4, y = 1.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SAWBLADE_MEDIUM)
        b.add(ObjectType.COIN, 3.6f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 70f, clearance = 3.8f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.5f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.5f)
        b.add(ObjectType.COIN, 4f) // Coin 3
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_cycles",
            name = "Cycles",
            description = "Spinning sawblades and tight ship caverns test your navigation skills.",
            difficulty = Difficulty.HARD,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF0D1B2A,
            groundColor = 0xFF1B263B,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1800L
        )
    }

    // 10. XSTEP (Hard 6★) - 275 units long (~26.5s)
    private fun createXStep(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 1.8f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 70f, clearance = 4f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.8f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 6, y = 2f)
        b.add(ObjectType.COIN, 3.5f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_xstep",
            name = "xStep",
            description = "Blue gravity rings, breakable rhythm blocks, and dynamic color shifts.",
            difficulty = Difficulty.HARD,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF140D2A,
            groundColor = 0xFF24154A,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1900L
        )
    }

    // 11. CLUTTERFUNK (Harder 7★) - 280 units long (~27s)
    private fun createClutterfunk(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.SAWBLADE_GIANT, 2f, advanceAfter = 8f)
        b.addPlatform(length = 5, y = 1.5f)
        b.add(ObjectType.COIN, 3.6f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 75f, clearance = 3.8f, hasObstacles = true)
        b.add(ObjectType.COIN, 4f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_RED, orbY = 2.2f)
        b.addPlatform(length = 7, y = 2f)
        b.add(ObjectType.COIN, 3.8f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_clutterfunk",
            name = "Clutterfunk",
            description = "High octane hazard density! Giant spinning saws and chaotic beat drops.",
            difficulty = Difficulty.HARDER,
            stars = 7,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF220A10,
            groundColor = 0xFF3D101C,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2000L
        )
    }

    // 12. THEORY OF EVERYTHING (Harder 8★) - 285 units long (~27.5s)
    private fun createTheoryOfEverything(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 6, y = 1.5f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.add(ObjectType.COIN, 3.8f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 80f, clearance = 4f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.5f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.2f)
        b.addPlatform(length = 8, y = 1.8f)
        b.add(ObjectType.COIN, 3.6f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_theory_of_everything",
            name = "Theory of Everything",
            description = "The philosophical masterpiece of geometry, flight, and timing.",
            difficulty = Difficulty.HARDER,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF051821,
            groundColor = 0xFF0D3244,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2100L
        )
    }

    // 13. ELECTROMAN (Harder 8★) - 290 units long (~28s)
    private fun createElectroman(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.COIN, 3.5f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 80f, clearance = 4f, hasObstacles = true)
        b.add(ObjectType.COIN, 4f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.add(ObjectType.COIN, 3.8f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_electroman",
            name = "Electroman Adventures",
            description = "Electro synth beats with breakable platforms and razor-sharp spikes.",
            difficulty = Difficulty.HARDER,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1F1200,
            groundColor = 0xFF3D2400,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2200L
        )
    }

    // 14. CLUBSTEP (Insane 9★) - 295 units long (~28.5s)
    private fun createClubstep(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 2f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.SAWBLADE_GIANT, 2.5f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 3.8f) // Coin 1

        b.addShipSection(lengthUnits = 85f, clearance = 3.6f, hasObstacles = true)
        b.add(ObjectType.COIN, 4f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2f)
        b.addPlatform(length = 8, y = 2f)
        b.add(ObjectType.COIN, 4.2f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The original Demon challenge! Infernal ship corridors and demon faces.",
            difficulty = Difficulty.INSANE,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF2A0000,
            groundColor = 0xFF4A0000,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2300L
        )
    }

    // 15. ELECTRODYNAMIX (Insane 9★) - 300 units long (~29s)
    private fun createElectrodynamix(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_3X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.COIN, 3.5f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 90f, clearance = 3.8f, hasObstacles = true)
        b.add(ObjectType.COIN, 4f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.add(ObjectType.COIN, 3.8f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_electrodynamix",
            name = "Electrodynamix",
            description = "Insane 3x speed bursts and razor-sharp ship navigation through neon storms.",
            difficulty = Difficulty.INSANE,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF0D0A2A,
            groundColor = 0xFF1E1452,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2400L
        )
    }

    // 16. HEXAGON FORCE (Insane 9★) - 305 units long (~29.5s)
    private fun createHexagonForce(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 6, y = 1.5f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.add(ObjectType.COIN, 3.8f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 90f, clearance = 3.8f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.5f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 8, y = 1.8f)
        b.add(ObjectType.COIN, 4f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_hexagon_force",
            name = "Hexagon Force",
            description = "Dual portals and slope platforming across intricate geometric grids.",
            difficulty = Difficulty.INSANE,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF001B2E,
            groundColor = 0xFF003049,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2500L
        )
    }

    // 17. BLAST PROCESSING (Normal 4★ - accessible fun wave/ship) - 300 units long (~29s)
    private fun createBlastProcessing(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 2f)
        b.addPlatform(length = 7, y = 1.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.8f) // Coin 1

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)
        b.addShipSection(lengthUnits = 90f, clearance = 4.5f, hasObstacles = false)
        b.add(ObjectType.COIN, 3.6f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.add(ObjectType.COIN, 3.5f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_blast_processing",
            name = "Blast Processing",
            description = "BWOMP! Smooth accessible flow with wide ship flight and rewarding rhythm.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF002920,
            groundColor = 0xFF004D3C,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2600L
        )
    }

    // 18. DEADLOCKED (Demon 10★) - 310 units long (~30s)
    private fun createDeadlocked(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 2f)
        b.add(ObjectType.PORTAL_SPEED_3X, 1f, advanceAfter = 4f)
        b.add(ObjectType.SAWBLADE_GIANT, 2f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 4f) // Coin 1

        b.addShipSection(lengthUnits = 95f, clearance = 3.5f, hasObstacles = true)
        b.add(ObjectType.COIN, 3.8f) // Coin 2

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.2f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.4f)
        b.addPlatform(length = 9, y = 2f)
        b.add(ObjectType.COIN, 4.5f) // Coin 3
        b.advance(20f)

        return Level(
            id = "main_deadlocked",
            name = "Deadlocked",
            description = "The ultimate Demon showdown! Rapid speed changes, tight ship waves, and monster hazards.",
            difficulty = Difficulty.DEMON,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF2B0000,
            groundColor = 0xFF450000,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2700L
        )
    }
}
