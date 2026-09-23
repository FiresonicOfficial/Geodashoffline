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
            createDeadlocked()
        )
    }

    // 1. STEREO MADNESS (Easy 1★)
    private fun createStereoMadness(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Warm-up single spikes
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f

        // Pyramid platform with Coin 1
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.5f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.0f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4.5f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.75f, y = 3.5f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 6.0f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 7.5f, y = 0f, type = ObjectType.BLOCK))
        x += 12f

        // Yellow pad over spikes
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 1.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        // Elevated bridge
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 1.5f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 1.5f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        x += 11f

        // High pillar with Coin 2
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 3.2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.5f, y = 4.5f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE))
        x += 8.5f

        // Yellow orb airborne rhythm
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 6.5f, y = 0f, type = ObjectType.SPIKE))
        x += 11f

        // Final sprint with Coin 3
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.2f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.6f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.0f, y = 1.6f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 5.2f, y = 0f, type = ObjectType.SPIKE))
        x += 9f

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The classic original journey. Master basic timing, blocks, and bounce pads.",
            difficulty = Difficulty.EASY,
            stars = 1,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF0D1B2A,
            groundColor = 0xFF1B263B,
            objects = objects,
            isUnlocked = true,
            createdAt = 1000L
        )
    }

    // 2. BACK ON TRACK (Easy 2★)
    private fun createBackOnTrack(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Yellow jump pads
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        // Elevated bounce sequences
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.4f, y = 2f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 5.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 3.4f, y = 3.6f, type = ObjectType.COIN)) // Coin 1
        x += 11f

        // Double spike leaps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 5.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 6.5f

        // Pink bounce pads
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 2f, y = 1.2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.5f, y = 2.4f, type = ObjectType.COIN)) // Coin 2
        x += 9f

        // Sawblade introduction
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 1f, type = ObjectType.SAWBLADE_SMALL))
        objects.add(GameObject(x = x + 4.5f, y = 0f, type = ObjectType.BLOCK))
        x += 8.5f

        // Coin 3 finish
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 2.8f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        return Level(
            id = "main_back_on_track",
            name = "Back On Track",
            description = "Bouncy rhythm mechanics! Yellow and pink jump pads over spike pits.",
            difficulty = Difficulty.EASY,
            stars = 2,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF003049,
            groundColor = 0xFF023E8A,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Stereo Madness or 1 Star",
            createdAt = 1100L
        )
    }

    // 3. POLARGEIST (Normal 3★)
    private fun createPolargeist(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Yellow Jump Rings in mid-air
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.6f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        // Multi-ring chain with Coin 1
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 2.4f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 4.4f, y = 3.6f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 2.0f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 9.5f

        // Platforming with small saw hazards
        objects.add(GameObject(x = x, y = 1.2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.2f, y = 1.2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.8f, y = 1.5f, type = ObjectType.SAWBLADE_SMALL))
        objects.add(GameObject(x = x + 4.4f, y = 1.2f, type = ObjectType.BLOCK))
        x += 9.5f

        // Pink orb micro jump with Coin 2
        objects.add(GameObject(x = x, y = 1.4f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 2.5f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 2.2f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        // Triple spike test
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 8f

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
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Back On Track or 3 Stars",
            createdAt = 1200L
        )
    }

    // 4. DRY OUT (Normal 4★)
    private fun createDryOut(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Step pyramids
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.5f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.0f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8.5f

        // Inverted gravity portal!
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.PORTAL_GRAVITY_INVERT))
        x += 4f

        // Upside down ceiling platforming with hanging spikes
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 6.5f, type = ObjectType.BLOCK_DARK))
            objects.add(GameObject(x = x + (i * 1.5f), y = 5.5f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + (i * 1.5f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x + 3f, y = 4.2f, type = ObjectType.COIN)) // Coin 1
        x += 11f

        // Normal gravity return
        objects.add(GameObject(x = x, y = 5.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 5f

        // Rotating sawblade hop
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 1f, type = ObjectType.SAWBLADE_MEDIUM))
        objects.add(GameObject(x = x + 4.5f, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4.5f, y = 2.2f, type = ObjectType.COIN)) // Coin 2
        x += 9f

        // Triple spike finale
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Defy gravity! Inverted perspective and ceiling platforming.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF28112B,
            groundColor = 0xFF431238,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Polargeist or 6 Stars",
            createdAt = 1300L
        )
    }

    // 5. BASE AFTER BASE (Hard 5★)
    private fun createBaseAfterBase(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Ship Portal Section
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_SHIP))
        x += 5f

        // Ship navigation corridor
        for (i in 0..4) {
            val yOffset = if (i % 2 == 0) 1f else 3f
            objects.add(GameObject(x = x + (i * 4f), y = yOffset, type = ObjectType.SPIKE_SMALL))
            objects.add(GameObject(x = x + (i * 4f), y = yOffset + 3.8f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 8f, y = 2.5f, type = ObjectType.COIN)) // Coin 1
        x += 24f

        // Return to Cube mode
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        x += 5f

        // Dark blocks rhythm and sawblades
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 1.5f, y = 1.2f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 3.0f, y = 1.2f, type = ObjectType.SAWBLADE_MEDIUM))
        objects.add(GameObject(x = x + 5.0f, y = 1.2f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 5.0f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        x += 10f

        // Triple spike leaps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7.5f

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "Rocket ship mode unlocked! Master smooth flight and dark block leaps.",
            difficulty = Difficulty.HARD,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF1B1B1E,
            groundColor = 0xFF2E2E38,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Dry Out or 8 Stars",
            createdAt = 1400L
        )
    }

    // 6. CAN'T LET GO (Harder 6★)
    private fun createCantLetGo(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Authentic dark menacing aesthetic, tight black pad leaps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 5.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        // Black block towers with hanging spikes
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 1.2f, y = 2f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 4.0f, y = 2f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 6.0f, y = 3.5f, type = ObjectType.COIN)) // Coin 1
        x += 10f

        // Gravity flip section with authentic spikes
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_GRAVITY_INVERT))
        x += 4f
        objects.add(GameObject(x = x, y = 5.5f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 2f, y = 5.5f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 4f, y = 4.2f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 6f, y = 5.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 10f

        // Sawblade gauntlet
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 1.2f, type = ObjectType.SAWBLADE_LARGE))
        objects.add(GameObject(x = x + 4.8f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 9.5f

        return Level(
            id = "main_cant_let_go",
            name = "Can't Let Go",
            description = "Dark themes, inverted micro-timings, and unforgiving dark block traps.",
            difficulty = Difficulty.HARDER,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF140D1E,
            groundColor = 0xFF2A153A,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Base After Base or 10 Stars",
            createdAt = 1500L
        )
    }

    // 7. JUMPER (Harder 7★)
    private fun createJumper(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Rapid bouncing sequences
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 2.2f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 5.0f, y = 4.0f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 6.8f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        objects.add(GameObject(x = x + 5.0f, y = 5.2f, type = ObjectType.COIN)) // Coin 1
        x += 11f

        // High altitude leaps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.4f, y = 2.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.6f, y = 2.5f, type = ObjectType.SAWBLADE_MEDIUM))
        objects.add(GameObject(x = x + 5.8f, y = 2.5f, type = ObjectType.BLOCK))
        x += 10f

        // Speed 2x portal jump
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        return Level(
            id = "main_jumper",
            name = "Jumper",
            description = "High energy trampoline leaps, mid-air ring pivots, and rapid vertical gameplay.",
            difficulty = Difficulty.HARDER,
            stars = 7,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF03254C,
            groundColor = 0xFF1167B1,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Can't Let Go or 14 Stars",
            createdAt = 1600L
        )
    }

    // 8. TIME MACHINE (NEW - Insane 8★)
    private fun createTimeMachine(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Famous triple spike test!
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7.5f

        // Triple spike directly after block drop!
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.2f, y = 1.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.8f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        objects.add(GameObject(x = x + 2.0f, y = 3.2f, type = ObjectType.COIN)) // Coin 1
        x += 8f

        // Triple jump orbs over spike beds
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 2.0f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 3.6f, y = 1.6f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 4.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 9.5f

        // Ship section with narrow sawtooth pillars
        objects.add(GameObject(x = x, y = 2.5f, type = ObjectType.PORTAL_SHIP))
        x += 5f
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 4.5f), y = 0.8f, type = ObjectType.SAWBLADE_SMALL))
            objects.add(GameObject(x = x + (i * 4.5f), y = 5.2f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 9f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        x += 26f

        // Return to Cube
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        x += 5f

        // Quad spike with red mega pad!
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_FOUR))
        objects.add(GameObject(x = x + 2.5f, y = 3.8f, type = ObjectType.COIN)) // Coin 3
        x += 9f

        return Level(
            id = "main_time_machine",
            name = "Time Machine",
            description = "The birthplace of the iconic Triple Spike. Precise late jumping is required.",
            difficulty = Difficulty.INSANE,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF2A0845,
            groundColor = 0xFF6441A5,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Jumper or 18 Stars",
            createdAt = 1700L
        )
    }

    // 9. CYCLES (NEW - Insane 9★)
    private fun createCycles(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Alternating gravity portals with rapid platform taps
        objects.add(GameObject(x = x, y = 1.2f, type = ObjectType.PORTAL_GRAVITY_INVERT))
        x += 4f
        objects.add(GameObject(x = x, y = 6.5f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 1.5f, y = 6.5f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 3.0f, y = 5.5f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 4.5f, y = 5.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 9f

        // Landing directly on yellow bounce pad
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        objects.add(GameObject(x = x + 2.2f, y = 3.5f, type = ObjectType.COIN)) // Coin 1
        x += 8f

        // Rotating sawblade tunnel
        for (i in 0..3) {
            objects.add(GameObject(x = x + (i * 3.5f), y = 1.5f, type = ObjectType.SAWBLADE_MEDIUM))
            objects.add(GameObject(x = x + (i * 3.5f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        x += 16f

        // Blue gravity ring airborne cascade
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_BLUE))
        objects.add(GameObject(x = x + 2.4f, y = 5.2f, type = ObjectType.ORB_BLUE))
        objects.add(GameObject(x = x + 4.8f, y = 1.6f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.5f, y = 3.5f, type = ObjectType.COIN)) // Coin 2
        x += 9f

        // Final triple spike leap
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        return Level(
            id = "main_cycles",
            name = "Cycles",
            description = "Rapid cyclical gravity shifts. Jump between ceiling and floor at high velocity.",
            difficulty = Difficulty.INSANE,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF191D32,
            groundColor = 0xFF282F44,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Time Machine or 22 Stars",
            createdAt = 1800L
        )
    }

    // 10. XSTEP (NEW - Insane 10★)
    private fun createXStep(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Fast pad combinations and sawblades
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 1.8f, y = 1.5f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 4.2f, y = 3.5f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        objects.add(GameObject(x = x + 4.2f, y = 4.8f, type = ObjectType.COIN)) // Coin 1
        x += 10f

        // Floating neon outline blocks over sawblade sea
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 2.2f), y = 2.0f, type = ObjectType.BLOCK_OUTLINE))
            objects.add(GameObject(x = x + (i * 2.2f), y = 0f, type = ObjectType.SAWBLADE_MEDIUM))
        }
        x += 14f

        // Speed 2x portal sprint
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SAWBLADE_LARGE))
        objects.add(GameObject(x = x + 2.5f, y = 4.2f, type = ObjectType.COIN)) // Coin 2
        x += 9f

        return Level(
            id = "main_xstep",
            name = "xStep",
            description = "High octane pad combos, suspended floating steps, and spinning sawblades.",
            difficulty = Difficulty.INSANE,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF0D2818,
            groundColor = 0xFF04471C,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Cycles or 26 Stars",
            createdAt = 1900L
        )
    }

    // 11. CLUTTERFUNK (NEW - Insane 11★)
    private fun createClutterfunk(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Frantic rhythm with sawblade gauntlets
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SAWBLADE_MEDIUM))
        x += 4.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        // Rapid small leaps
        for (i in 0..3) {
            objects.add(GameObject(x = x + (i * 2.0f), y = 1.2f, type = ObjectType.HALF_BLOCK))
            objects.add(GameObject(x = x + (i * 2.0f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        x += 10f

        // Red Mega Orb into Black Slam Orb combo!
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.ORB_RED))
        objects.add(GameObject(x = x + 2.4f, y = 4.5f, type = ObjectType.ORB_BLACK))
        objects.add(GameObject(x = x + 3.8f, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.4f, y = 5.8f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 5.0f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 11f

        // Tight ship flying through saw maze
        objects.add(GameObject(x = x, y = 2.5f, type = ObjectType.PORTAL_SHIP))
        x += 5f
        for (i in 0..5) {
            val ySaw = if (i % 2 == 0) 1f else 4f
            objects.add(GameObject(x = x + (i * 4f), y = ySaw, type = ObjectType.SAWBLADE_MEDIUM))
        }
        objects.add(GameObject(x = x + 10f, y = 2.6f, type = ObjectType.COIN)) // Coin 2
        x += 28f

        // Return to Cube
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        x += 5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        return Level(
            id = "main_clutterfunk",
            name = "Clutterfunk",
            description = "Frantic industrial techno gauntlet. Precision miniature leaps and sawblade mazes.",
            difficulty = Difficulty.INSANE,
            stars = 11,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF2B0909,
            groundColor = 0xFF590D0D,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on xStep or 30 Stars",
            createdAt = 2000L
        )
    }

    // 12. THEORY OF EVERYTHING (NEW - Insane 12★)
    private fun createTheoryOfEverything(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Multi-portal sequence
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 2.8f, type = ObjectType.ORB_GREEN))
        objects.add(GameObject(x = x + 4.5f, y = 5.2f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 5.5f, y = 4.2f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 6.5f, y = 0f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 12f

        // Neon outline pillars & saw hazards
        for (i in 0..3) {
            objects.add(GameObject(x = x + (i * 3.5f), y = 2.0f, type = ObjectType.BLOCK_OUTLINE))
            objects.add(GameObject(x = x + (i * 3.5f), y = 0f, type = ObjectType.SAWBLADE_LARGE))
        }
        x += 16f

        // Tight ship navigation corridor
        objects.add(GameObject(x = x, y = 3f, type = ObjectType.PORTAL_SHIP))
        x += 5f
        for (i in 0..5) {
            val yOffset = if (i % 2 == 0) 1.2f else 3.2f
            objects.add(GameObject(x = x + (i * 4f), y = yOffset, type = ObjectType.SPIKE_SMALL))
            objects.add(GameObject(x = x + (i * 4f), y = yOffset + 3.0f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 10f, y = 2.6f, type = ObjectType.COIN)) // Coin 2
        x += 28f

        // Final Cube Sprint with Triple Spikes
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        x += 5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        return Level(
            id = "main_theory_of_everything",
            name = "Theory of Everything",
            description = "A cosmic puzzle of fast portal transitions, reverse gravity, and ship flight.",
            difficulty = Difficulty.INSANE,
            stars = 12,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF0B132B,
            groundColor = 0xFF1C2541,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Clutterfunk or 35 Stars",
            createdAt = 2100L
        )
    }

    // 13. ELECTROMAN ADVENTURES (NEW - Insane 10★)
    private fun createElectroman(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Rotating saw gauntlet with red pads
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 1.0f, type = ObjectType.SAWBLADE_LARGE))
        objects.add(GameObject(x = x + 5.2f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        objects.add(GameObject(x = x + 2.5f, y = 4.2f, type = ObjectType.COIN)) // Coin 1
        x += 10f

        // Black slam orb timing
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_RED))
        objects.add(GameObject(x = x + 2.4f, y = 4.6f, type = ObjectType.ORB_BLACK))
        objects.add(GameObject(x = x + 3.6f, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 9.5f

        // Rapid sawblade field
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 2.8f), y = 0f, type = ObjectType.SAWBLADE_MEDIUM))
            objects.add(GameObject(x = x + (i * 2.8f), y = 3.5f, type = ObjectType.HALF_BLOCK))
        }
        x += 16f

        // Triple spike finish
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        return Level(
            id = "main_electroman",
            name = "Electroman Adventures",
            description = "Electrifying sawblade fields, high vertical leaps, and black orb drops.",
            difficulty = Difficulty.INSANE,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF240046,
            groundColor = 0xFF3C096C,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Theory of Everything or 40 Stars",
            createdAt = 2200L
        )
    }

    // 14. CLUBSTEP (Official Demon 14★)
    private fun createClubstep(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Speed 2x intro with triple spike
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 7f

        // Precise airborne orb combos
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 1.8f, y = 2.4f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 1.8f, y = 3.8f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 2.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 4.0f, y = 1.8f, type = ObjectType.ORB_BLUE)) // Flip to ceiling!
        x += 9f

        // Upside-down demon teeth
        for (i in 0..5) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 7f, type = ObjectType.BLOCK_DARK))
            objects.add(GameObject(x = x + (i * 1.5f), y = 6f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + (i * 1.5f), y = 0f, type = ObjectType.SPIKE))
        }
        x += 12f

        // Ship Demon Flight through narrow corridors
        objects.add(GameObject(x = x, y = 3f, type = ObjectType.PORTAL_SHIP))
        x += 5f

        for (step in 0..6) {
            val yOffset = if (step % 2 == 0) 1.2f else 3.2f
            objects.add(GameObject(x = x + (step * 4.5f), y = yOffset, type = ObjectType.SAWBLADE_MEDIUM))
            objects.add(GameObject(x = x + (step * 4.5f), y = yOffset + 3.2f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 9f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 20f, y = 2.2f, type = ObjectType.COIN)) // Coin 3
        x += 34f

        // Final Cube Sprint with Quad Spikes!
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        objects.add(GameObject(x = x + 1.5f, y = 2f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 5f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_FOUR))
        x += 8f

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The legendary Demon test. Monster jaws, tight 1.8-gap flight corridors, and relentless spikes.",
            difficulty = Difficulty.DEMON,
            stars = 14,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1F0303,
            groundColor = 0xFF3D0606,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Earn 50% on Electroman Adventures or 45 Stars",
            createdAt = 6000L
        )
    }

    // 15. DEADLOCKED (NEW - Extreme Demon 15★)
    private fun createDeadlocked(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // 3x Hyperspeed Portal Opening!
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_SPEED_3X))
        x += 4f

        // Hyperspeed triple spike leaps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 9f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 9f

        // Red Mega Pad launch over giant dual sawblades
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 1.0f, type = ObjectType.SAWBLADE_LARGE))
        objects.add(GameObject(x = x + 4.5f, y = 1.0f, type = ObjectType.SAWBLADE_LARGE))
        objects.add(GameObject(x = x + 3.5f, y = 4.8f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 7.5f, y = 0f, type = ObjectType.BLOCK_DARK))
        x += 12f

        // Black Slam Orb micro-landing
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_RED))
        objects.add(GameObject(x = x + 2.5f, y = 5.0f, type = ObjectType.ORB_BLACK))
        objects.add(GameObject(x = x + 3.8f, y = 0f, type = ObjectType.HALF_BLOCK))
        objects.add(GameObject(x = x + 5.2f, y = 0f, type = ObjectType.SPIKE_FOUR))
        x += 11f

        // Extreme Demon Ship Corridor at 3x speed!
        objects.add(GameObject(x = x, y = 3f, type = ObjectType.PORTAL_SHIP))
        x += 5f
        for (i in 0..7) {
            val yOffset = if (i % 2 == 0) 1.2f else 3.0f
            objects.add(GameObject(x = x + (i * 4.8f), y = yOffset, type = ObjectType.SAWBLADE_LARGE))
            objects.add(GameObject(x = x + (i * 4.8f), y = yOffset + 3.5f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 12f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 28f, y = 2.2f, type = ObjectType.COIN)) // Coin 3
        x += 44f

        // Final Cube Hyperspeed Gauntlet
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        x += 5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_RED))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE_FOUR))
        objects.add(GameObject(x = x + 6.8f, y = 0f, type = ObjectType.SPIKE_TRIPLE))
        x += 12f

        return Level(
            id = "main_deadlocked",
            name = "Deadlocked",
            description = "The ultimate test of human reflexes. 3x hyperspeed, dual rotating sawblades, and razor-sharp ship control.",
            difficulty = Difficulty.DEMON,
            stars = 15,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1A0000,
            groundColor = 0xFF4D0000,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Conquer Clubstep or earn 50 Stars",
            createdAt = 7000L
        )
    }
}
