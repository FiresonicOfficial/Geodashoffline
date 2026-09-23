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
            createClubstep()
        )
    }

    private fun createStereoMadness(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Initial warm-up jumps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 6f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))

        // Step up platform with Secret Coin 1
        x += 7f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 2.5f, type = ObjectType.COIN)) // Coin 1

        // Drop down into yellow pad
        x += 6f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4f, y = 0f, type = ObjectType.SPIKE)) // Triple spike avoided by pad!

        // Platform bridge over hazard
        x += 7f
        for (i in 0..4) {
            objects.add(GameObject(x = x + i, y = 1.5f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + i, y = 0f, type = ObjectType.SPIKE))
        }

        // Secret Coin 2 on high pillar
        x += 7f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 3f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 4.2f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))

        // Orb jump sequence
        x += 7f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4f, y = 0f, type = ObjectType.SPIKE))

        // Final gauntlet with Coin 3
        x += 6f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4f, y = 1.2f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 5f, y = 0f, type = ObjectType.SPIKE))

        // Final steps
        x += 7f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The classic opening rhythm run. Learn the core jump physics, pads, and coin paths!",
            difficulty = Difficulty.EASY,
            stars = 1,
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

        // Introduces Pink Pad (gentler jump)
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.SPIKE))
        x += 6f

        // Multi orb jumps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.7f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 3.5f, y = 1.7f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 5f, y = 0f, type = ObjectType.SPIKE))
        x += 7f

        // Platform stair jumps
        for (i in 0..2) {
            objects.add(GameObject(x = x + (i * 2.5f), y = i * 1f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + (i * 2.5f) + 1f, y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x + 5f, y = 3.5f, type = ObjectType.COIN)) // Coin 1
        x += 9f

        // Long jump over pit
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        for (i in 1..4) {
            objects.add(GameObject(x = x + i, y = 0f, type = ObjectType.SPIKE))
        }
        objects.add(GameObject(x = x + 5.5f, y = 0f, type = ObjectType.BLOCK))
        x += 7f

        // Orb rhythm chain
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2.5f, y = 1.8f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 2.5f, y = 3.2f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4.5f, y = 0f, type = ObjectType.SPIKE))
        x += 7f

        // High tower jump
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 4f, y = 2f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5f, y = 1.5f, type = ObjectType.COIN)) // Coin 3
        x += 8f

        return Level(
            id = "main_back_on_track",
            name = "Back On Track",
            description = "Get into the groove with pink bounce pads and mid-air ring taps.",
            difficulty = Difficulty.NORMAL,
            stars = 2,
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

        // Introduce Gravity Pad and double jump rings
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))
        x += 5f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 2.5f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 6f

        // Gravity flip pad to ceiling platforms!
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_GRAVITY))
        objects.add(GameObject(x = x + 1f, y = 6f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 6f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3f, y = 6f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2f, y = 5f, type = ObjectType.COIN)) // Coin 1 (upside down)
        objects.add(GameObject(x = x + 4f, y = 6f, type = ObjectType.PAD_GRAVITY)) // Flip back
        x += 7f

        // Hanging spikes underneath platforms
        for (i in 0..3) {
            objects.add(GameObject(x = x + i, y = 3f, type = ObjectType.BLOCK))
            objects.add(GameObject(x = x + i, y = 2f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + i, y = 0f, type = ObjectType.SPIKE_SMALL))
        }
        objects.add(GameObject(x = x - 1f, y = 0f, type = ObjectType.PAD_YELLOW))
        x += 7f

        // Blue Orb (gravity flip on tap)
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_BLUE))
        objects.add(GameObject(x = x + 2f, y = 5.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 3f, y = 5.5f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.5f, y = 4.2f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4f, y = 5.5f, type = ObjectType.ORB_BLUE)) // Flip back down
        x += 7f

        // Triple spike with precise jump orb
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1f, y = 1.7f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 3f, type = ObjectType.COIN)) // Coin 3
        x += 8f

        return Level(
            id = "main_polargeist",
            name = "Polargeist",
            description = "Master gravity pads and mid-air blue gravity orbs in an icy cyber matrix.",
            difficulty = Difficulty.HARD,
            stars = 3,
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

        // Speed portal 2x
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PORTAL_SPEED_2X))
        x += 4f

        // Fast jumps
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 4f
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        x += 4f

        // Gravity Invert Portal (Full upside-down gameplay!)
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_GRAVITY_INVERT))
        x += 3f

        // Upside down obstacles on ceiling (y = 7f)
        for (i in 0..6) {
            objects.add(GameObject(x = x + i, y = 7f, type = ObjectType.BLOCK))
        }
        objects.add(GameObject(x = x + 2f, y = 6f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 4f, y = 6f, type = ObjectType.SPIKE_HANGING))
        objects.add(GameObject(x = x + 3f, y = 4.5f, type = ObjectType.COIN)) // Coin 1
        x += 8f

        // Flip back down
        objects.add(GameObject(x = x, y = 4f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.PORTAL_SPEED_1X))
        x += 4f

        // Stepping blocks over spikes
        objects.add(GameObject(x = x, y = 1f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 1.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 2.5f, y = 2f, type = ObjectType.BLOCK))
        objects.add(GameObject(x = x + 2.5f, y = 3.2f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 3.5f, y = 0f, type = ObjectType.SPIKE_DUAL))
        objects.add(GameObject(x = x + 4.5f, y = 1f, type = ObjectType.BLOCK))
        x += 7f

        // Green Orb (Jump + gravity invert!)
        objects.add(GameObject(x = x, y = 1.8f, type = ObjectType.ORB_GREEN))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3f, y = 5.5f, type = ObjectType.ORB_GREEN))
        objects.add(GameObject(x = x + 3f, y = 3.5f, type = ObjectType.COIN)) // Coin 3
        x += 8f

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Gravity invert portals turn the world upside down. Test your dual-perspective reflexes!",
            difficulty = Difficulty.HARDER,
            stars = 4,
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

    private fun createBaseAfterBase(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Cube section warmup
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1.5f, y = 1.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE_DUAL))
        x += 6f

        // Enter Ship Mode!
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_SHIP))
        x += 4f

        // Ship obstacles: pillars with gaps to fly through
        for (col in 0..3) {
            val gapY = if (col % 2 == 0) 3.5f else 2.0f
            // Ceiling pillar
            for (y in 5..7) {
                if (y.toFloat() != gapY && y.toFloat() != gapY + 1f) {
                    objects.add(GameObject(x = x + (col * 5f), y = y.toFloat(), type = ObjectType.BLOCK_DARK))
                }
            }
            // Floor pillar
            for (y in 0..1) {
                objects.add(GameObject(x = x + (col * 5f), y = y.toFloat(), type = ObjectType.SPIKE_SMALL))
            }
        }
        objects.add(GameObject(x = x + 8f, y = 3.5f, type = ObjectType.COIN)) // Coin 1 in ship gap
        x += 22f

        // Exit back to Cube Mode
        objects.add(GameObject(x = x, y = 1.5f, type = ObjectType.PORTAL_CUBE))
        x += 4f

        // Rapid pad chain with Coin 2 & 3
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 3.5f, type = ObjectType.PAD_PINK))
        objects.add(GameObject(x = x + 2.5f, y = 5f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 4f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 5f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 2.5f, type = ObjectType.COIN)) // Coin 3
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "Hold to fly in the Ship rocket section, then land back as a cube for rapid jump chains.",
            difficulty = Difficulty.INSANE,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF2A0826,
            groundColor = 0xFF4A1043,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Reach 50% on Dry Out or earn 10 Stars",
            createdAt = 5000L
        )
    }

    private fun createClubstep(): Level {
        val objects = mutableListOf<GameObject>()
        var x = 8f

        // Fast pace 2x speed demon entry
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PORTAL_SPEED_2X))
        x += 3f

        // Tight spikes
        objects.add(GameObject(x = x, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 1f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 2f, y = 0f, type = ObjectType.SPIKE)) // Triple spike at 2x!
        x += 5f

        // Precise orb sequence
        objects.add(GameObject(x = x, y = 1.6f, type = ObjectType.ORB_PINK))
        objects.add(GameObject(x = x + 1.8f, y = 2.2f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 2.5f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 3.5f, y = 1.8f, type = ObjectType.ORB_BLUE)) // Flip to ceiling!
        x += 6f

        // Upside down demon teeth
        for (i in 0..4) {
            objects.add(GameObject(x = x + i, y = 7f, type = ObjectType.BLOCK_DARK))
            objects.add(GameObject(x = x + i, y = 6f, type = ObjectType.SPIKE_HANGING))
            objects.add(GameObject(x = x + i, y = 0f, type = ObjectType.SPIKE))
        }
        objects.add(GameObject(x = x + 2f, y = 3f, type = ObjectType.COIN)) // Coin 1
        x += 7f

        // Ship Demon Flight
        objects.add(GameObject(x = x, y = 3f, type = ObjectType.PORTAL_SHIP))
        x += 4f

        // Demon jaws flight: narrow corridors
        for (step in 0..4) {
            val yOffset = if (step % 2 == 0) 1f else 3f
            objects.add(GameObject(x = x + (step * 4f), y = yOffset, type = ObjectType.SPIKE))
            objects.add(GameObject(x = x + (step * 4f), y = yOffset + 3.5f, type = ObjectType.SPIKE_HANGING))
        }
        objects.add(GameObject(x = x + 6f, y = 2.8f, type = ObjectType.COIN)) // Coin 2
        objects.add(GameObject(x = x + 14f, y = 2.2f, type = ObjectType.COIN)) // Coin 3
        x += 22f

        // Final Cube sprint
        objects.add(GameObject(x = x, y = 2f, type = ObjectType.PORTAL_CUBE))
        objects.add(GameObject(x = x + 1f, y = 2f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
        x += 4f

        objects.add(GameObject(x = x, y = 0f, type = ObjectType.PAD_YELLOW))
        objects.add(GameObject(x = x + 2f, y = 2.8f, type = ObjectType.ORB_YELLOW))
        objects.add(GameObject(x = x + 3f, y = 0f, type = ObjectType.SPIKE))
        objects.add(GameObject(x = x + 4f, y = 0f, type = ObjectType.SPIKE))
        x += 8f

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The ultimate demon trial. Navigate tight spike corridors, speed shifts, and demon jaws!",
            difficulty = Difficulty.DEMON,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1A0505,
            groundColor = 0xFF380909,
            objects = objects,
            isUnlocked = false,
            unlockRequirement = "Complete Base After Base or earn 14 Stars",
            createdAt = 6000L
        )
    }
}
