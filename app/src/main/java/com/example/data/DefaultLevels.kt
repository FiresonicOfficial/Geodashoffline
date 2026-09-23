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
            createCantLetGo(),
            createJumper(),
            createBaseAfterBase(),
            createClubstep()
        )
    }

    private fun createStereoMadness(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Intro warm-up jumps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f

        // Section 2: Step-up pyramid platform with Secret Coin 1
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.5f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.0f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4.5f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.75f, y = 3.5f, type = ObjectType.COIN)) // Coin 1 (Apex of Pyramid)
        objects.add(GameObject(x = x + 6.0f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 7.5f, y = 0f, type = ObjectType.BLOCK))
        x += 12f

        // Section 3: Yellow bounce pads over spike beds
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 1.8f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2.8f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.8f, y = 0f, type = ObjectType.SPIKE)) // Clear triple spike!
        x += 8f

        // Section 4: Elevated bridge with hanging hazards
        for (i in 0..5) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 1.5f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 1.5f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        x += 12f

        // Section 5: High pillar jump with Secret Coin 2
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 3.2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.5f, y = 4.5f, type = ObjectType.COIN)) // Coin 2 (Sky high)
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE))
        x += 8.5f

        // Section 6: Yellow ring airborne rhythm chain
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 6.5f, y = 0f, type = ObjectType.SPIKE))
        x += 11f

        // Section 7: Mid-level platforming and steps
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.2f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.6f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.0f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 5.2f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 6.6f, y = 0f, type = ObjectType.SPIKE))
        x += 11f

        // Section 8: Final Sprint with Secret Coin 3
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.5f, y = 1.6f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.SPIKE))
        x += 9f

        // Final pad leap to triumph
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.2f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The classic opening rhythm run. Master the core jump physics, pads, and secret coin paths!",
            difficulty = Difficulty.EASY,
            stars = 2,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF0D1B2A,
            groundColor = 0xFF1B263B,
            objects = objects,
            isUnlocked = true,
            unlockRequirement = "Unlocked by default",
            createdAt = 1000L
        )
    }

    private fun createBackOnTrack(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Pink pad introduction (gentler hop)
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 1.8f, y = 0f, type = ObjectType.SPIKE))
        x += 7f

        // Section 2: Alternating yellow and pink orbs
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.6f, y = 1.7f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 3.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.SPIKE))
        x += 9f

        // Section 3: Tiered staircase with Secret Coin 1
        for (i in 0..3) {
            objects.add(GameObject(x = x + (i * 2.2f), y = i * 1f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 2.2f) + 1f, y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x + 6.6f, y = 4.2f, type = ObjectType.COIN)) // Coin 1
        x += 12f

        // Section 4: Yellow pad leap over wide hazard
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        for (i in 1..3) {
            objects.add(GameObject(x = x + (i * 1.2f), y = 0f, type = ObjectType.SPIKE))
        }
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.BLOCK))
        x += 8.5f

        // Section 5: Pink orb sync chain with Secret Coin 2
        objects.add(GameObject(x = x, y = 1.7f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 1.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2.8f, y = 2.0f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.0f, y = 3.6f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5.4f, y = 0f, type = ObjectType.SPIKE))
        x += 10.5f

        // Section 6: Double spike hurdles and floating blocks
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.5f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.0f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5.5f, y = 1f, type = ObjectType.BLOCK))
        x += 10f

        // Section 7: Suspended bridge & Final pad tower with Secret Coin 3
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 2.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.4f, y = 2.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4.6f, y = 2.5f, type = ObjectType.SPIKE_SMALL))
        objects.add(GameObject(x = x + 5.8f, y = 2.0f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 7.0f, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 9.0f, y = 0f, type = ObjectType.SPIKE))
        x += 13f

        return Level(
            id = "main_back_on_track",
            name = "Back On Track",
            description = "Get into the groove with pink bounce pads and mid-air ring taps.",
            difficulty = Difficulty.NORMAL,
            stars = 3,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF1F1235,
            groundColor = 0xFF361B5E,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Stereo Madness or earn 1 Star",
            createdAt = 2000L
        )
    }

    private fun createPolargeist(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Arctic warm-up
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.2f, y = 0f, type = ObjectType.SPIKE))
        x += 7f

        // Section 2: Gravity pad launch to ceiling run
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_GRAVITY))
        for (i in 0..4) {
            objects.add(GameObject(x = x + 1.5f + (i * 1.5f), y = 6.5f, type = ObjectType.BLOCK))
        }
        objects.add(GameObject(x = x + 4.5f, y = 5.2f, type = ObjectType.COIN)) // Coin 1 (Inverted ceiling)
        objects.add(GameObject(x = x + 8.0f, y = 6.5f, type = ObjectType.PAD_GRAVITY)) // Flip back to floor
        x += 12f

        // Section 3: Hanging stalactites over floor spikes
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 1.4f), y = 3.2f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 1.4f), y = 2.2f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + (i * 1.4f), y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x - 1f, y = 0f, type = ObjectType.PAD_YELLOW))
        x += 11f

        // Section 4: Blue gravity orb airborne flip
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_BLUE))
        objects.add(GameObject(x = x + 2.2f, y = 5.8f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.4f, y = 5.8f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.8f, y = 4.4f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4.6f, y = 5.8f, type = ObjectType.ORB_BLUE)) // Flip down
        x += 10.5f

        // Section 5: Double spike precision with yellow orb
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2.4f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.2f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        x += 8.5f

        // Section 6: Stepping platforms and precision drop with Secret Coin 3
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.8f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.6f, y = 3f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.6f, y = 4.4f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 5.4f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 6.6f, y = 0f, type = ObjectType.PAD_PINK))
        x += 12f

        // Section 7: Final icy sprint
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 2f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_polargeist",
            name = "Polargeist",
            description = "Master gravity pads and mid-air blue gravity orbs in an icy cyber matrix.",
            difficulty = Difficulty.HARD,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 0,
            bgColor = 0xFF00293C,
            groundColor = 0xFF004466,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Back On Track or earn 3 Stars",
            createdAt = 3000L
        )
    }

    private fun createDryOut(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Speed boost
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PORTAL_SPEED_2X))
        x += 5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 5f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 5f

        // Section 2: Gravity Invert Portal (Full upside-down ceiling world)
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_GRAVITY_INVERT))
        x += 4f

        for (i in 0..7) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 7f, type = ObjectType.BLOCK))
        }
        objects.add(GameObject(x = x + 3f, y = 5.8f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 6f, y = 5.8f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 4.5f, y = 4.2f, type = ObjectType.COIN)) // Coin 1 (Inverted ceiling)
        x += 14f

        // Section 3: Return to normal gravity & speed
        objects.add(GameObject(x = x, y = 4.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.PORTAL_SPEED_1X))
        x += 6f

        // Section 4: Stepping blocks with dual spikes
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.8f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 3.2f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.2f, y = 3.5f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4.6f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 6.0f, y = 1f, type = ObjectType.BLOCK))
        x += 11f

        // Section 5: Green Orb (jump + gravity flip!)
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_GREEN))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.8f, y = 5.5f, type = ObjectType.ORB_GREEN))
        objects.add(GameObject(x = x + 3.8f, y = 3.5f, type = ObjectType.COIN)) // Coin 3
        x += 10f

        // Section 6: Desert canyon sprint
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.7f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5.0f, y = 1f, type = ObjectType.BLOCK))
        x += 9f

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Gravity invert portals turn the world upside down. Test your dual-perspective reflexes!",
            difficulty = Difficulty.HARDER,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF2D1E0F,
            groundColor = 0xFF523315,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Polargeist or earn 6 Stars",
            createdAt = 4000L
        )
    }

    private fun createCantLetGo(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Dark theme intro with precision single-block steps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6f
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.SPIKE))
        x += 6.5f

        // Section 2: Dark blocks over bottomless spike bed with Secret Coin 1
        for (i in 0..4) {
            objects.add(GameObject(x = x + (i * 2.2f), y = 1.5f, type = ObjectType.BLOCK_DARK))
            objects.add(GameObject(x = x + (i * 2.2f), y = 0f, type = ObjectType.SPIKE))
        }
        objects.add(GameObject(x = x + 4.4f, y = 3.0f, type = ObjectType.COIN)) // Coin 1
        x += 13f

        // Section 3: Alternating hanging spikes and jump pads
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 2.0f, y = 3.5f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 3.8f, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.SPIKE))
        x += 10.5f

        // Section 4: Upside-down gravity jump sequence with Secret Coin 2
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_GRAVITY))
        for (i in 0..4) {
            objects.add(GameObject(x = x + 1.5f + (i * 1.5f), y = 6.5f, type = ObjectType.BLOCK_DARK))
        }
        objects.add(GameObject(x = x + 4.5f, y = 5.0f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 7.5f, y = 6.5f, type = ObjectType.PAD_GRAVITY))
        x += 12f

        // Section 5: Precision orbs over triple spike gap
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.0f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.5f, y = 1.8f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 6.0f, y = 0f, type = ObjectType.SPIKE))
        x += 10.5f

        // Section 6: Dark maze bridge with Secret Coin 3
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 1.5f, y = 2f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 3.0f, y = 3f, type = ObjectType.BLOCK_DARK))
        objects.add(GameObject(x = x + 3.0f, y = 4.4f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 4.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 6.0f, y = 0f, type = ObjectType.PAD_YELLOW))
        x += 11f

        return Level(
            id = "main_cant_let_go",
            name = "Can't Let Go",
            description = "Dark blocks, tight precision platforms, and unforgiving spike pits in the iconic stage.",
            difficulty = Difficulty.HARDER,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF14141E,
            groundColor = 0xFF242433,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Dry Out or earn 10 Stars",
            createdAt = 4500L
        )
    }

    private fun createJumper(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Fast rhythmic pad hops
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3.7f, y = 2f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 6.0f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 10f

        // Section 2: Aerial pad chain with Secret Coin 1
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.2f, y = 3f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.2f, y = 4.4f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 3.4f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        x += 10.5f

        // Section 3: Jungle ceiling hop bridge
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_GRAVITY))
        for (i in 0..4) {
            objects.add(GameObject(x = x + 1.5f + (i * 1.5f), y = 6.5f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + 1.5f + (i * 1.5f), y = 5.5f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 7.5f, y = 6.5f, type = ObjectType.PAD_GRAVITY))
        x += 12f

        // Section 4: Mid-air triple ring sync with Secret Coin 2
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.0f, y = 2.4f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 3.0f, y = 3.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 6.0f, y = 0f, type = ObjectType.SPIKE))
        x += 11f

        // Section 5: Rapid staircase descent with Secret Coin 3
        for (i in 0..3) {
            objects.add(GameObject(x = x + (i * 2.2f), y = (3 - i) * 1f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 2.2f) + 1f, y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x + 2.2f, y = 3.6f, type = ObjectType.COIN)) // Coin 3
        x += 12f

        // Section 6: High-energy final pad bounce
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.7f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_jumper",
            name = "Jumper",
            description = "High-flying leaps, bouncy pad combos, and synchronized air ring taps!",
            difficulty = Difficulty.INSANE,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF0A2618,
            groundColor = 0xFF13422B,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Can't Let Go or earn 14 Stars",
            createdAt = 4800L
        )
    }

    private fun createBaseAfterBase(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: Cube section warmup
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.8f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 8f

        // Section 2: Enter Ship Mode!
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_SHIP))
        x += 5f

        // Section 3: Ship flight cavern with undulating heights and Secret Coin 1 & 2
        for (col in 0..6) {
            val gapY = if (col % 2 == 0) 3.5f else 2.2f
            for (y in 5..7) {
                if (y.toFloat() != gapY && y.toFloat() != gapY + 1f) {
                    objects.add(GameObject(x = x + (col * 5f), y = y.toFloat(), type = ObjectType.BLOCK_DARK))
                }
            }
            for (y in 0..1) {
                objects.add(GameObject(x = x + (col * 5f), y = y.toFloat(), type = ObjectType.SPIKE_SMALL))
            }
        }
        objects.add(GameObject(x = x + 10f, y = 3.5f, type = ObjectType.COIN)) // Coin 1 (Center cavern)
        objects.add(GameObject(x = x + 22f, y = 2.4f, type = ObjectType.COIN)) // Coin 2 (Low tunnel)
        x += 35f

        // Section 4: Exit Ship back to Cube Mode
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_CUBE))
        x += 5f

        // Section 5: Rapid pad chain with Secret Coin 3
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.8f, y = 3.2f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 2.8f, y = 4.8f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 4.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5.7f, y = 0f, type = ObjectType.SPIKE))
        x += 11f

        // Section 6: Final dash to victory
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.4f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "Fly through the extended ship flight cavern, then land back as a cube for rapid jump chains.",
            difficulty = Difficulty.INSANE,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF2A0826,
            groundColor = 0xFF4A1043,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Jumper or earn 18 Stars",
            createdAt = 5000L
        )
    }

    private fun createClubstep(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Section 1: 2x Speed Demon Entry
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2.4f, y = 0f, type = ObjectType.SPIKE)) // Triple spike at 2x!
        x += 7f

        // Section 2: Precise airborne orb combos with Secret Coin 1
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 1.8f, y = 2.4f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 1.8f, y = 3.8f, type = ObjectType.COIN)) // Coin 1
        objects.add(GameObject(x = x + 2.8f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.0f, y = 1.8f, type = ObjectType.ORB_BLUE)) // Flip to ceiling!
        x += 9f

        // Section 3: Upside-down demon teeth
        for (i in 0..5) {
            objects.add(GameObject(x = x + (i * 1.5f), y = 7f, type = ObjectType.BLOCK_DARK))
            objects.add(GameObject(x = x + (i * 1.5f), y = 6f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + (i * 1.5f), y = 0f, type = ObjectType.SPIKE))
        }
        x += 12f

        // Section 4: Ship Demon Flight through narrow corridors
        objects.add(GameObject(x = x, y = 3f, type = ObjectType.PORTAL_SHIP))
        x += 5f

        for (step in 0..6) {
            val yOffset = if (step % 2 == 0) 1.2f else 3.2f
            objects.add(GameObject(x = x + (step * 4.5f), y = yOffset, type = ObjectType.SPIKE))
            objects.add(GameObject(x = x + (step * 4.5f), y = yOffset + 3.2f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 9f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 20f, y = 2.2f, type = ObjectType.COIN)) // Coin 3
        x += 32f

        // Section 5: Final Cube Sprint
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        objects.add(GameObject(x = x + 1.5f, y = 2f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 5f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.4f, y = 2.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.6f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.8f, y = 0f, type = ObjectType.SPIKE))
        x += 9f

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The ultimate demon trial. Navigate tight spike corridors, speed shifts, and demon jaws!",
            difficulty = Difficulty.DEMON,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1F0303,
            groundColor = 0xFF3D0606,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Complete Base After Base or earn 24 Stars",
            createdAt = 6000L
        )
    }
}
