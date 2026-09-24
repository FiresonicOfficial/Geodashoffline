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

    // Helper to generate full-length (>=30 seconds), fair, rhythmically tight sequences
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

        fun addPlatform(
            length: Int,
            y: Float,
            stepWidth: Float = 1.2f,
            blockType: ObjectType = ObjectType.BLOCK,
            hasGroundHazard: Boolean = false,
            hasDecoChains: Boolean = false
        ) {
            for (i in 0 until length) {
                val px = cursorX + (i * stepWidth)
                objects.add(GameObject(x = px, y = y, type = blockType))
                if (hasGroundHazard && i % 2 == 0) {
                    objects.add(GameObject(x = px, y = 0f, type = ObjectType.SPIKE_SMALL))
                }
                if (hasDecoChains && i == 0 && y > 1f) {
                    objects.add(GameObject(x = px, y = y - 1.5f, type = ObjectType.DECO_CHAIN))
                }
            }
            cursorX += length * stepWidth + 2f
        }

        fun addStairSequence(
            steps: Int,
            startY: Float = 0f,
            blockType: ObjectType = ObjectType.BLOCK,
            withCoins: Boolean = false
        ) {
            for (i in 0 until steps) {
                objects.add(GameObject(x = cursorX, y = startY + i, type = blockType))
                cursorX += 1.4f
            }
            if (withCoins) {
                objects.add(GameObject(x = cursorX - 1.4f, y = startY + steps + 1.2f, type = ObjectType.COIN))
            }
            cursorX += 3.5f
        }

        fun addJumpPadOverHazard(
            padType: ObjectType,
            hazardType: ObjectType,
            padY: Float = 0f,
            withWarning: Boolean = false
        ) {
            if (withWarning) {
                objects.add(GameObject(x = cursorX - 1.5f, y = padY + 1.2f, type = ObjectType.DECO_WARNING_SIGN))
            }
            objects.add(GameObject(x = cursorX, y = padY, type = padType))
            objects.add(GameObject(x = cursorX + 2.2f, y = 0f, type = hazardType))
            cursorX += 7.5f
        }

        fun addOrbSequence(
            orbType: ObjectType,
            orbY: Float = 1.8f,
            groundSpike: Boolean = true,
            withPulseDeco: Boolean = false
        ) {
            if (groundSpike) {
                objects.add(GameObject(x = cursorX + 0.8f, y = 0f, type = ObjectType.SPIKE))
            }
            if (withPulseDeco) {
                objects.add(GameObject(x = cursorX + 0.8f, y = orbY + 1.5f, type = ObjectType.DECO_PULSE_RING))
            }
            objects.add(GameObject(x = cursorX + 0.8f, y = orbY, type = orbType))
            cursorX += 6.5f
        }

        fun addSawRun(sawCount: Int, y: Float = 0f, sawType: ObjectType = ObjectType.SAWBLADE_MEDIUM) {
            for (i in 0 until sawCount) {
                objects.add(GameObject(x = cursorX, y = y, type = sawType))
                cursorX += 3.5f
            }
            cursorX += 3f
        }

        // ==========================================
        // DIVERSE SHIP SECTION STYLES
        // ==========================================

        fun addClassicOpenShip(lengthUnits: Float, clearance: Float = 4.6f) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 5f

            val startX = cursorX
            var alt = false
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if ((cursorX - startX).toInt() % 16 == 0) {
                    val obsY = if (alt) 4.2f else 2.0f
                    objects.add(GameObject(x = cursorX, y = obsY, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 1.2f, y = obsY, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX - 1.5f, y = obsY + 0.2f, type = ObjectType.DECO_WARNING_SIGN))
                    objects.add(GameObject(x = cursorX + 0.6f, y = if (obsY > 3f) 2f else 5.5f, type = ObjectType.DECO_STAR))
                    alt = !alt
                }
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addBouncyCavernShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3.2f, type = ObjectType.DECO_PULSE_RING))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.2f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 5 == 0) {
                    objects.add(GameObject(x = cursorX, y = 5.2f, type = ObjectType.DECO_CHAIN))
                }

                if (step % 8 == 0) {
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 1.5f, type = ObjectType.PAD_YELLOW))
                    objects.add(GameObject(x = cursorX + 2.4f, y = 1.5f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 3.6f, y = 2.5f, type = ObjectType.DECO_CRYSTAL))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addIceStalactiteShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_CRYSTAL))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_OUTLINE))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 6 == 0) {
                    objects.add(GameObject(x = cursorX, y = 6.5f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 6.5f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 0.6f, y = 5.2f, type = ObjectType.DECO_CRYSTAL))
                } else if (step % 6 == 3) {
                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_OUTLINE))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 0.5f, type = ObjectType.DECO_CRYSTAL))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addGravityFlipShip(lengthUnits: Float, flipInterval: Float = 22f) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            cursorX += 5f

            val startX = cursorX
            var inverted = false
            var nextFlipAt = startX + 14f

            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (cursorX >= nextFlipAt && cursorX < startX + lengthUnits - 15f) {
                    inverted = !inverted
                    val portalType = if (inverted) ObjectType.PORTAL_GRAVITY_INVERT else ObjectType.PORTAL_GRAVITY_NORMAL
                    val arrowType = if (inverted) ObjectType.DECO_ARROW_UP else ObjectType.DECO_ARROW_DOWN
                    objects.add(GameObject(x = cursorX, y = 2.5f, type = portalType))
                    objects.add(GameObject(x = cursorX + 1.5f, y = 3f, type = arrowType))
                    objects.add(GameObject(x = cursorX - 1.2f, y = 3f, type = ObjectType.DECO_WARNING_SIGN))
                    nextFlipAt = cursorX + flipInterval
                    cursorX += 4f
                    continue
                }

                if ((cursorX - startX).toInt() % 14 == 0) {
                    if (inverted) {
                        objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 4.5f, type = ObjectType.SPIKE_HANGING))
                    } else {
                        objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.SPIKE))
                    }
                }
                cursorX += 2.5f
            }

            if (inverted) {
                objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
                cursorX += 4f
            }
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addTechSpeedShip(lengthUnits: Float, speed: ObjectType = ObjectType.PORTAL_SPEED_2X) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 1.5f, y = 2f, type = speed))
            objects.add(GameObject(x = cursorX + 3f, y = 3f, type = ObjectType.DECO_TECH_CIRCUIT))
            cursorX += 6f

            val startX = cursorX
            var slotHigh = true
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if ((cursorX - startX).toInt() % 18 == 0) {
                    objects.add(GameObject(x = cursorX - 2f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
                    if (slotHigh) {
                        objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.SPIKE))
                        objects.add(GameObject(x = cursorX + 1.2f, y = 4.8f, type = ObjectType.DECO_NEON_ARROW))
                    } else {
                        objects.add(GameObject(x = cursorX, y = 6.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 4.5f, type = ObjectType.SPIKE_HANGING))
                        objects.add(GameObject(x = cursorX + 1.2f, y = 2.2f, type = ObjectType.DECO_NEON_ARROW))
                    }
                    objects.add(GameObject(x = cursorX + 1.5f, y = 6.8f, type = ObjectType.DECO_TECH_CIRCUIT))
                    slotHigh = !slotHigh
                }
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
            objects.add(GameObject(x = cursorX + 2f, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addJaggedTeethShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 4.5f, type = ObjectType.DECO_CHAIN))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 4 == 0) {
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.DECO_CHAIN))
                }

                if (step % 7 == 0) {
                    objects.add(GameObject(x = cursorX, y = 6.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.SPIKE_HANGING))
                } else if (step % 7 == 4) {
                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        fun addDemonMonsterJawsShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 4f, type = ObjectType.DECO_MONSTER_EYE))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 12 == 0) {
                    objects.add(GameObject(x = cursorX, y = 6.2f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 5.2f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX, y = 7.6f, type = ObjectType.DECO_MONSTER_EYE))

                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                    objects.add(GameObject(x = cursorX - 2f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
                } else if (step % 12 == 6) {
                    objects.add(GameObject(x = cursorX, y = 3.8f, type = ObjectType.SAWBLADE_LARGE))
                    objects.add(GameObject(x = cursorX + 1.5f, y = 2.0f, type = ObjectType.DECO_PULSE_RING))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        // ==========================================
        // NEW: WAVE & UFO GAME MODES
        // ==========================================

        /** WAVE SECTION: 45° diagonal darting flight between obstacles */
        fun addWaveSection(
            lengthUnits: Float,
            speed: ObjectType = ObjectType.PORTAL_SPEED_1X,
            tightness: Float = 14f
        ) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_WAVE))
            if (speed != ObjectType.PORTAL_SPEED_1X) {
                objects.add(GameObject(x = cursorX + 1.2f, y = 2f, type = speed))
            }
            objects.add(GameObject(x = cursorX + 2.2f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 5f

            val startX = cursorX
            var zigUp = true
            while (cursorX < startX + lengthUnits) {
                // Wave boundary walls
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_OUTLINE))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                val dist = (cursorX - startX).toInt()
                if (dist % tightness.toInt() == 0) {
                    if (zigUp) {
                        objects.add(GameObject(x = cursorX, y = 4.8f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX + 1.2f, y = 4.8f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX + 0.6f, y = 2.2f, type = ObjectType.DECO_STAR))
                        objects.add(GameObject(x = cursorX + 0.6f, y = 2.2f, type = ObjectType.DECO_PULSE_RING))
                    } else {
                        objects.add(GameObject(x = cursorX, y = 1.2f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX + 1.2f, y = 1.2f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX + 0.6f, y = 4.6f, type = ObjectType.DECO_STAR))
                        objects.add(GameObject(x = cursorX + 0.6f, y = 4.6f, type = ObjectType.DECO_PULSE_RING))
                    }
                    zigUp = !zigUp
                }
                cursorX += 2.5f
            }

            if (speed != ObjectType.PORTAL_SPEED_1X) {
                objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
                cursorX += 2f
            }
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** UFO SECTION: Flappy-style mid-air hopping through hazard corridors */
        fun addUfoSection(
            lengthUnits: Float,
            speed: ObjectType = ObjectType.PORTAL_SPEED_1X
        ) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_UFO))
            if (speed != ObjectType.PORTAL_SPEED_1X) {
                objects.add(GameObject(x = cursorX + 1.2f, y = 2f, type = speed))
            }
            objects.add(GameObject(x = cursorX + 2.2f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 5 == 0) {
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.DECO_CHAIN))
                }

                if (step % 12 == 0) {
                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                    objects.add(GameObject(x = cursorX, y = 6.5f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 3.5f, type = ObjectType.DECO_PULSE_RING))
                } else if (step % 12 == 6) {
                    objects.add(GameObject(x = cursorX, y = 3.2f, type = ObjectType.SAWBLADE_MEDIUM))
                    objects.add(GameObject(x = cursorX - 1.5f, y = 3.2f, type = ObjectType.DECO_WARNING_SIGN))
                }
                step++
                cursorX += 2.5f
            }

            if (speed != ObjectType.PORTAL_SPEED_1X) {
                objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
                cursorX += 2f
            }
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }
    }

    // ==========================================
    // 18 CAMPAIGN LEVELS (EACH >= 30 SECONDS!)
    // ==========================================

    // 1. STEREO MADNESS (Easy 1★) - ~345 units (~33s)
    private fun createStereoMadness(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Classic Cube Intro (0 - 90)
        b.add(ObjectType.DECO_NEON_ARROW, 1.5f, advanceAfter = 3f)
        b.add(ObjectType.SPIKE, 0f, advanceAfter = 8f)
        b.add(ObjectType.SPIKE, 0f, advanceAfter = 8f)
        b.addStairSequence(steps = 3, startY = 0f, withCoins = true) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL, withWarning = true)
        b.addPlatform(length = 5, y = 1.5f, hasGroundHazard = true, hasDecoChains = true)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f, withPulseDeco = true)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE)

        // Color trigger: Deep Blue to Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Part 2: Open Ship Flight (90 - 180)
        b.addClassicOpenShip(lengthUnits = 85f)
        b.add(ObjectType.COIN, 2.5f, advanceAfter = 4f) // Coin 2

        // Color trigger: Cyan to Purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_PURPLE, 0.5f)

        // Part 3: Intermediate Cube Platforming (180 - 270)
        b.addPlatform(length = 6, y = 2f, hasGroundHazard = true)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.addStairSequence(steps = 4, startY = 0f)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.addPlatform(length = 5, y = 1.5f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 3

        // Part 4: Grand Finale Sprint (270 - 345)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 7, y = 2f, blockType = ObjectType.BLOCK_RAINBOW)
        b.add(ObjectType.DECO_STAR, 3.5f, advanceAfter = 4f)
        b.addStairSequence(steps = 3, startY = 0f, blockType = ObjectType.BLOCK_RAINBOW)
        b.advance(20f)

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The iconic introductory campaign. Full 33s length with accessible cube timing and open ship flight.",
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

    // 2. BACK ON TRACK (Easy 2★) - ~350 units (~34s)
    private fun createBackOnTrack(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Bouncy Cube Intro (0 - 95)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL, withWarning = true)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.4f)
        b.add(ObjectType.BLOCK, 2f)
        b.add(ObjectType.COIN, 3.6f, advanceAfter = 1.2f) // Coin 1
        b.add(ObjectType.PAD_YELLOW, 2f, advanceAfter = 6f)
        b.addPlatform(length = 5, y = 1.5f, hasDecoChains = true)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.8f)

        // Trigger: Teal to Green
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_GREEN, 0.5f)

        // Part 2: Bouncy Cavern Ship (95 - 190)
        b.addBouncyCavernShip(lengthUnits = 90f)
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 2

        // Trigger: Green to Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Part 3: Pad Jump Combinations (190 - 280)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 6, y = 2f, hasGroundHazard = true)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.addPlatform(length = 5, y = 1.5f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 3

        // Part 4: Finish Stretch (280 - 350)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.advance(22f)

        return Level(
            id = "main_back_on_track",
            name = "Back On Track",
            description = "Bouncy rhythm mechanics! Yellow and pink jump pads over spike pits with bouncy ship cavern.",
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

    // 3. POLARGEIST (Normal 3★) - ~355 units (~34s)
    private fun createPolargeist(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Frost Cube & Jump Rings (0 - 95)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f, withPulseDeco = true)
        b.add(ObjectType.ORB_PINK, 1.8f, advanceAfter = 2.5f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_OUTLINE)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)

        // Trigger: Cyan Frost to Deep Violet
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: Ice Stalactite Ship (95 - 190)
        b.addIceStalactiteShip(lengthUnits = 90f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Violet to Dark Frost
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_DARK, 0.5f)

        // Part 3: High-Altitude Ice Platforms (190 - 280)
        b.addPlatform(length = 5, y = 2.5f, blockType = ObjectType.BLOCK_OUTLINE)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 3.2f)
        b.addPlatform(length = 6, y = 1.5f, hasGroundHazard = true)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.6f, advanceAfter = 4f) // Coin 3

        // Part 4: Icicle Finish (280 - 355)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f)
        b.addPlatform(length = 8, y = 1.8f, blockType = ObjectType.BLOCK_OUTLINE)
        b.advance(22f)

        return Level(
            id = "main_polargeist",
            name = "Polargeist",
            description = "Ice cavern atmosphere! Jump rings over freezing hazards and hanging stalactite ship navigation.",
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

    // 4. DRY OUT (Normal 3★) - ~360 units (~35s)
    private fun createDryOut(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Desert Cube (0 - 75)
        b.addPlatform(length = 5, y = 1f, blockType = ObjectType.BLOCK_DARK)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 1
        b.addPlatform(length = 4, y = 1.5f)

        // Trigger: Desert Amber to Crimson
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 2: Inverted Gravity Cube (75 - 150)
        b.add(ObjectType.PORTAL_GRAVITY_INVERT, 1.5f, advanceAfter = 5f)
        for (i in 0..5) {
            b.add(ObjectType.BLOCK_DARK, 6.5f)
            b.add(ObjectType.SPIKE_HANGING, 5.5f)
            b.advance(2.2f)
        }
        b.add(ObjectType.COIN, 4.5f, advanceAfter = 4f) // Coin 2
        b.add(ObjectType.PORTAL_GRAVITY_NORMAL, 5.5f, advanceAfter = 6f)

        // Part 3: Gravity Flip Ship (150 - 250)
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addGravityFlipShip(lengthUnits = 95f, flipInterval = 22f)

        // Part 4: Returning Cube Gauntlet (250 - 360)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 3
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.advance(25f)

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Upside-down gravity flips! Experience inverted flight in the ship part and inverted roof platforming.",
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

    // 5. BASE AFTER BASE (Normal 4★) - ~375 units (~35s)
    private fun createBaseAfterBase(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Tech Base Cube (0 - 80)
        b.add(ObjectType.DECO_TECH_CIRCUIT, 1.5f, advanceAfter = 3f)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)

        // Trigger: Slate to Dark Purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: Tech Speed Ship with Laser Gate Slots (80 - 185)
        b.addTechSpeedShip(lengthUnits = 100f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 2

        // Trigger: Purple to Dark Slate
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_DARK, 0.5f)

        // Part 3: Cyber Fortress Cube (185 - 280)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_DARK)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 2.2f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.addPlatform(length = 5, y = 1.5f)

        // Part 4: Tech Speed Outro (280 - 375)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.advance(25f)

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "High-tech underground cyber base! Fast 2x speed laser gate ship navigation and tight platforming.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF111726,
            groundColor = 0xFF1D283C,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1400L
        )
    }

    // 6. CAN'T LET GO (Normal 4★) - ~365 units (~35s)
    private fun createCantLetGo(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Industrial Dark Cube (0 - 80)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_DARK, hasDecoChains = true)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.5f) // Slam orb!
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_DARK)

        // Trigger: Dark Crimson to Blood Red
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 2: Jagged Teeth Ship (80 - 180)
        b.addJaggedTeethShip(lengthUnits = 95f)
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 2

        // Trigger: Blood Red to Pitch Black
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_RED, 0.5f)

        // Part 3: Black Slam Orb Combos (180 - 280)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.8f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.addPlatform(length = 5, y = 2f)

        // Part 4: Claustrophobic Escape (280 - 365)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.advance(25f)

        return Level(
            id = "main_cant_let_go",
            name = "Can't Let Go",
            description = "Claustrophobic atmosphere with jagged teeth spikes, black downward slam orbs, and industrial chains.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 1,
            bgColor = 0xFF1A0A10,
            groundColor = 0xFF2E0F1A,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1500L
        )
    }

    // 7. JUMPER (Hard 5★) - ~370 units (~35s)
    private fun createJumper(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: High Meadow Bounce (0 - 85)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 5, y = 2.5f, blockType = ObjectType.BLOCK_GRID)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.add(ObjectType.COIN, 4.2f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f, withPulseDeco = true)

        // Trigger: Emerald to Lime Green
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)

        // Part 2: Platform Maze Ship (85 - 185)
        b.addClassicOpenShip(lengthUnits = 95f, clearance = 4.4f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Lime to Gold
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_GOLD, 0.5f)

        // Part 3: Aerial Chain Pads (185 - 280)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 6, y = 2.8f, blockType = ObjectType.BLOCK_GRID)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 3.2f)
        b.add(ObjectType.COIN, 4.5f, advanceAfter = 4f) // Coin 3
        b.addPlatform(length = 6, y = 1.5f)

        // Part 4: Rhythmic Springs Outro (280 - 370)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.8f)
        b.advance(25f)

        return Level(
            id = "main_jumper",
            name = "Jumper",
            description = "High-flying leaps! Soar across elevated bounce platforms, aerial chain pads, and maze-like flight corridors.",
            difficulty = Difficulty.HARD,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF052B14,
            groundColor = 0xFF0A4723,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1600L
        )
    }

    // 8. TIME MACHINE (Hard 5★) - ~385 units (~35s)
    private fun createTimeMachine(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Chrono Triple Spikes (0 - 90)
        b.add(ObjectType.SPIKE_TRIPLE, 0f, advanceAfter = 8f)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)

        // Trigger: Chrono Violet to Neon Orange
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)

        // Part 2: Chrono Speed Ship (90 - 195)
        b.addTechSpeedShip(lengthUnits = 100f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 2

        // Trigger: Neon Orange to Cyber Purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 3: Triple Spike Reverse Gauntlet (195 - 290)
        b.add(ObjectType.SPIKE_TRIPLE, 0f, advanceAfter = 8f)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_DARK)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 2.2f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.add(ObjectType.SPIKE_TRIPLE, 0f, advanceAfter = 8f)

        // Part 4: Temporal Finish (290 - 385)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.advance(25f)

        return Level(
            id = "main_time_machine",
            name = "Time Machine",
            description = "Triple spike challenges, rapid speed shifts, and reverse gravity maneuvers in deep chrono violet.",
            difficulty = Difficulty.HARD,
            stars = 5,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF200B3B,
            groundColor = 0xFF34105E,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1700L
        )
    }

    // 9. CYCLES (Hard 6★) - ~375 units (~36s) - FEATURING WAVE MODE PREMIERE!
    private fun createCycles(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Wheel of Fury Cube with Saws (0 - 85)
        b.addSawRun(sawCount = 2, y = 0f)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)

        // Trigger: Sunset Coral to Deep Crimson
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 2: WAVE MODE PREMIERE! (85 - 190)
        // High-speed 45° darting between sawblade barriers!
        b.addWaveSection(lengthUnits = 100f, speed = ObjectType.PORTAL_SPEED_1X)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Crimson to Dark Violet
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 3: Undulating Stairs & Green Gravity Rings (190 - 285)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2.2f) // Gravity flipping green ring!
        b.addPlatform(length = 6, y = 1.8f, blockType = ObjectType.BLOCK_GRID)
        b.addSawRun(sawCount = 2, y = 0f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Part 4: Spinning Saws Climax (285 - 375)
        b.addSawRun(sawCount = 3, y = 0f)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(25f)

        return Level(
            id = "main_cycles",
            name = "Cycles",
            description = "Rotating sawblades, undulating step corridors, and the thrilling premiere of the diagonal WAVE gamemode!",
            difficulty = Difficulty.HARD,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF2D1305,
            groundColor = 0xFF4A1F08,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1800L
        )
    }

    // 10. xSTEP (Hard 6★) - ~380 units (~36s)
    private fun createXStep(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Blue Gravity Rings Intro (0 - 85)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 1.8f) // Blue gravity ring!
        b.addPlatform(length = 5, y = 6.5f, blockType = ObjectType.BLOCK_OUTLINE) // Running on ceiling!
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 5.2f) // Flip back to floor
        b.add(ObjectType.COIN, 3.2f, advanceAfter = 4f) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Trigger: Electric Blue to Violet
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: Sawblade Gauntlet Ship (85 - 190)
        b.addClassicOpenShip(lengthUnits = 100f, clearance = 4.2f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Violet to Neon Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)

        // Part 3: High-Altitude Breakable Step Rhythm (190 - 290)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_OUTLINE)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 2.4f)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 4.8f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 3
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Part 4: Pulse Finale (290 - 380)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.8f, blockType = ObjectType.BLOCK_OUTLINE)
        b.advance(25f)

        return Level(
            id = "main_xstep",
            name = "xStep",
            description = "Blue gravity rings flip your gravity in mid-air! Synchronized multi-level steps and spinning saw gauntlets.",
            difficulty = Difficulty.HARD,
            stars = 6,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF08183A,
            groundColor = 0xFF112D69,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 1900L
        )
    }

    // 11. CLUTTERFUNK (Harder 7★) - ~390 units (~37s) - FEATURING UFO MODE PREMIERE!
    private fun createClutterfunk(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Industrial Chaos Cube (0 - 85)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.addJumpPadOverHazard(ObjectType.PAD_PURPLE, ObjectType.SPIKE) // Purple mini pad!
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addSawRun(sawCount = 2, y = 0f)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.5f)

        // Trigger: Molten Orange to Dark Charcoal
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)

        // Part 2: UFO MODE PREMIERE! (85 - 195)
        // Flappy-bird hops between floating sawblade columns & laser gates!
        b.addUfoSection(lengthUnits = 105f, speed = ObjectType.PORTAL_SPEED_1X)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Charcoal to Molten Crimson
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_RED, 0.5f)

        // Part 3: Chaos Rhythm & Mini Pads (195 - 295)
        b.addJumpPadOverHazard(ObjectType.PAD_PURPLE, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_DARK)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.addSawRun(sawCount = 2, y = 0f)

        // Part 4: Chaos Factory Outro (295 - 390)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.advance(25f)

        return Level(
            id = "main_clutterfunk",
            name = "Clutterfunk",
            description = "Industrial molten chaos! Premiere of the flappy UFO gamemode hopping between suspended hazards.",
            difficulty = Difficulty.HARDER,
            stars = 7,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF261204,
            groundColor = 0xFF421E06,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2000L
        )
    }

    // 12. THEORY OF EVERYTHING (Harder 8★) - ~395 units (~38s) - COSMIC UFO & RAINBOW
    private fun createTheoryOfEverything(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Cosmic Nebula Cube (0 - 85)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_RAINBOW)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.2f) // Rainbow orb!
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 5, y = 2f, blockType = ObjectType.BLOCK_RAINBOW)

        // Trigger: Cosmic Astral to Magenta
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: Cosmic UFO Section (85 - 195)
        // Flapping through cosmic starlit rings and gravity portals!
        b.addUfoSection(lengthUnits = 105f, speed = ObjectType.PORTAL_SPEED_1X)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Magenta to Deep Space Navy
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Part 3: Cosmic Rainbow Flight (195 - 300)
        b.addClassicOpenShip(lengthUnits = 100f, clearance = 4.4f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3

        // Part 4: Astral Climax (300 - 395)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.8f, blockType = ObjectType.BLOCK_RAINBOW)
        b.advance(25f)

        return Level(
            id = "main_theory_of_everything",
            name = "Theory of Everything",
            description = "Cosmic mystery in deep space! UFO flight through floating star rings, rainbow blocks, and cosmic ships.",
            difficulty = Difficulty.HARDER,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF120826,
            groundColor = 0xFF24104B,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2100L
        )
    }

    // 13. ELECTROMAN ADVENTURES (Harder 8★) - ~390 units (~37s)
    private fun createElectroman(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: High-Voltage Grid Cube (0 - 85)
        b.add(ObjectType.DECO_TECH_CIRCUIT, 1.5f, advanceAfter = 3f)
        b.addSawRun(sawCount = 2, y = 0f, sawType = ObjectType.SAWBLADE_LARGE)
        b.addPlatform(length = 6, y = 1.8f, blockType = ObjectType.BLOCK_GRID)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_RED, ObjectType.SPIKE_TRIPLE) // High-power red pad!

        // Trigger: High-Voltage Gold to Cyber Purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: 2x Speed Tech Ship (85 - 195)
        b.addTechSpeedShip(lengthUnits = 105f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Cyber Purple to Neon Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Part 3: Electric Circuit Obstacles (195 - 295)
        b.addSawRun(sawCount = 2, y = 0f, sawType = ObjectType.SAWBLADE_MEDIUM)
        b.addPlatform(length = 6, y = 2f, blockType = ObjectType.BLOCK_GRID)
        b.addOrbSequence(ObjectType.ORB_RED, orbY = 2.4f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Part 4: Voltage Finale (295 - 390)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.advance(25f)

        return Level(
            id = "main_electroman",
            name = "Electroman Adventures",
            description = "High-voltage circuits, breakable neon grids, giant spinning saw gauntlets, and fast-paced tech ship flight.",
            difficulty = Difficulty.HARDER,
            stars = 8,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF061A2B,
            groundColor = 0xFF0D3252,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2200L
        )
    }

    // 14. CLUBSTEP (Demon 9★) - ~400 units (~38s)
    private fun createClubstep(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Demon Lair Cube (0 - 90)
        b.add(ObjectType.DECO_MONSTER_EYE, 2f, advanceAfter = 3f)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.add(ObjectType.SPIKE_TRIPLE, 0f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.4f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE)

        // Trigger: Blood Crimson to Abyssal Black
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_RED, 0.5f)

        // Part 2: Demon Monster Jaws Ship (90 - 200)
        // Tight flight through jaws with glowing demon eyes!
        b.addDemonMonsterJawsShip(lengthUnits = 105f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Abyssal Black to Inferno Red
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 3: Upside-Down Gravity Demon Jumps (200 - 300)
        b.add(ObjectType.PORTAL_GRAVITY_INVERT, 1.5f, advanceAfter = 4f)
        for (i in 0..5) {
            b.add(ObjectType.BLOCK_DARK, 6.5f)
            b.add(ObjectType.SPIKE_HANGING, 5.5f)
            b.advance(2.2f)
        }
        b.add(ObjectType.COIN, 4.5f, advanceAfter = 4f) // Coin 3
        b.add(ObjectType.PORTAL_GRAVITY_NORMAL, 5.5f, advanceAfter = 5f)
        b.addSawRun(sawCount = 2, y = 0f, sawType = ObjectType.SAWBLADE_LARGE)

        // Part 4: Demon Gauntlet Escape (300 - 400)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.advance(25f)

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The first true Demon test! Demon monster jaws with burning eyes, inverted ceiling platforming, and razor-sharp spikes.",
            difficulty = Difficulty.DEMON,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF140202,
            groundColor = 0xFF260505,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2300L
        )
    }

    // 15. ELECTRODYNAMIX (Demon 9★) - ~460 units (~34s at 3x/2x Speed!)
    private fun createElectrodynamix(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Fast Speed Cube (0 - 90)
        b.add(ObjectType.PORTAL_SPEED_2X, 1.5f, advanceAfter = 4f)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_OUTLINE)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addSawRun(sawCount = 2, y = 0f)

        // Trigger: Indigo to Neon Magenta
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: 3X SPEED STORM WAVE MODE! (90 - 230)
        // High-velocity lightning wave navigation!
        b.addWaveSection(lengthUnits = 135f, speed = ObjectType.PORTAL_SPEED_3X, tightness = 18f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Neon Magenta to Electric Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)

        // Part 3: Fast Storm Ship (230 - 360)
        b.addTechSpeedShip(lengthUnits = 125f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3

        // Part 4: Hyper Sonic Sprint (360 - 460)
        b.add(ObjectType.PORTAL_SPEED_3X, 1.5f, advanceAfter = 4f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_OUTLINE)
        b.add(ObjectType.PORTAL_SPEED_1X, 1.5f, advanceAfter = 4f)
        b.advance(25f)

        return Level(
            id = "main_electrodynamix",
            name = "Electrodynamix",
            description = "High-velocity adrenaline! Experience blistering 3x speed storm sections and hyper-fast wave darting.",
            difficulty = Difficulty.DEMON,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF0C0224,
            groundColor = 0xFF190545,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2400L
        )
    }

    // 16. HEXAGON FORCE (Demon 10★) - ~400 units (~38s) - DUAL HEX & UFO
    private fun createHexagonForce(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Dual Hex Cyberpunk Cube (0 - 90)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2.2f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        // Trigger: Amber to Purple
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)

        // Part 2: Hexagon UFO Mode (90 - 200)
        // Flapping through dual gravity gates & neon grids!
        b.addUfoSection(lengthUnits = 105f, speed = ObjectType.PORTAL_SPEED_1X)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Purple to Hexagon Gold
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_GOLD, 0.5f)

        // Part 3: Dual Gravity Flip Ship (200 - 300)
        b.addGravityFlipShip(lengthUnits = 95f, flipInterval = 20f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3

        // Part 4: Hexagonal Climax (300 - 400)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.8f, blockType = ObjectType.BLOCK_GRID)
        b.advance(25f)

        return Level(
            id = "main_hexagon_force",
            name = "Hexagon Force",
            description = "Dual geometric symmetry! Experience dual gravity inversions, UFO hopping, and intricate hex grids.",
            difficulty = Difficulty.INSANE,
            stars = 9,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF1F1602,
            groundColor = 0xFF362704,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2500L
        )
    }

    // 17. BLAST PROCESSING (Normal 4★) - ~410 units (~39s) - FEATURED MASTERCLASS WAVE!
    private fun createBlastProcessing(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Retro Synth Intro Cube (0 - 80)
        b.add(ObjectType.DECO_NEON_ARROW, 1.5f, advanceAfter = 3f)
        b.addPlatform(length = 6, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)

        // Trigger: Retro Teal to Synth Cyan
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Part 2: THE ICONIC BLAST PROCESSING WAVE (80 - 215)
        // "BWOMP!" Full-length wave masterclass zigzagging through retro tunnels!
        b.addWaveSection(lengthUnits = 130f, speed = ObjectType.PORTAL_SPEED_1X, tightness = 15f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 2

        // Trigger: Synth Cyan to Neon Green
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)

        // Part 3: Spacious Retro Ship Flight (215 - 315)
        b.addClassicOpenShip(lengthUnits = 95f, clearance = 4.8f)
        b.add(ObjectType.COIN, 4f, advanceAfter = 4f) // Coin 3

        // Part 4: Arcade Celebration Outro (315 - 410)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addPlatform(length = 8, y = 1.5f, blockType = ObjectType.BLOCK_GRID)
        b.advance(25f)

        return Level(
            id = "main_blast_processing",
            name = "Blast Processing",
            description = "BWOMP! The definitive Wave mode showcase with smooth retro synthwave corridors and rewarding rhythm.",
            difficulty = Difficulty.NORMAL,
            stars = 4,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 3,
            bgColor = 0xFF012421,
            groundColor = 0xFF023D38,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2600L
        )
    }

    // 18. DEADLOCKED (Extreme Demon 10★) - ~440 units (~38s) - CUBE + SHIP + UFO + WAVE!
    private fun createDeadlocked(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Part 1: Extreme Demon Intro Cube (0 - 80)
        b.add(ObjectType.PORTAL_SPEED_2X, 1.5f, advanceAfter = 4f)
        b.add(ObjectType.SAWBLADE_LARGE, 0f, advanceAfter = 6f)
        b.addPlatform(length = 5, y = 1.5f, blockType = ObjectType.BLOCK_DARK)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f) // Coin 1
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.4f)
        b.addJumpPadOverHazard(ObjectType.PAD_RED, ObjectType.SPIKE_TRIPLE)

        // Trigger: Blood Crimson
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 2: Demon Monster Jaws Ship (80 - 170)
        b.addDemonMonsterJawsShip(lengthUnits = 85f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 4f) // Coin 2

        // Trigger: Crimson to Toxic Green
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)

        // Part 3: Extreme Demon UFO Gauntlet (170 - 260)
        // High-tension hops through sawblades and monster teeth!
        b.addUfoSection(lengthUnits = 85f, speed = ObjectType.PORTAL_SPEED_1X)

        // Trigger: Toxic Green to Abyssal Obsidian
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.TRIGGER_GROUND_RED, 0.5f)

        // Part 4: 2X SPEED DEMON WAVE (260 - 350)
        // Razor-sharp darting between sawblade ceilings!
        b.addWaveSection(lengthUnits = 85f, speed = ObjectType.PORTAL_SPEED_2X, tightness = 16f)
        b.add(ObjectType.COIN, 4.2f, advanceAfter = 4f) // Coin 3

        // Trigger: Obsidian to Inferno Red
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Part 5: Final Apocalyptic Sprint (350 - 440)
        b.add(ObjectType.PORTAL_SPEED_3X, 1.5f, advanceAfter = 4f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE)
        b.addPlatform(length = 9, y = 1.8f, blockType = ObjectType.BLOCK_DARK)
        b.add(ObjectType.PORTAL_SPEED_1X, 1.5f, advanceAfter = 4f)
        b.advance(25f)

        return Level(
            id = "main_deadlocked",
            name = "Deadlocked",
            description = "The supreme Demon masterwork! A relentless gauntlet combining Cube, Ship, UFO, and Wave through demon monster jaws.",
            difficulty = Difficulty.DEMON,
            stars = 10,
            author = "RobTop / GeoDash",
            isCustom = false,
            musicTrack = 2,
            bgColor = 0xFF1C0000,
            groundColor = 0xFF330000,
            objects = b.objects,
            isUnlocked = true,
            createdAt = 2700L
        )
    }
}
