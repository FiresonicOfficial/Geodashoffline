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

    // Helper to generate a rhythmic, fair, accessible sequence with specialized ship variants and decorations
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

        fun addPlatform(length: Int, y: Float, stepWidth: Float = 1.2f, hasGroundHazard: Boolean = false, hasDecoChains: Boolean = false) {
            for (i in 0 until length) {
                val px = cursorX + (i * stepWidth)
                objects.add(GameObject(x = px, y = y, type = ObjectType.BLOCK))
                if (hasGroundHazard && i % 2 == 0) {
                    objects.add(GameObject(x = px, y = 0f, type = ObjectType.SPIKE_SMALL))
                }
                if (hasDecoChains && i == 0 && y > 1f) {
                    objects.add(GameObject(x = px, y = y - 1.5f, type = ObjectType.DECO_CHAIN))
                }
            }
            cursorX += length * stepWidth + 2f
        }

        fun addJumpPadOverHazard(padType: ObjectType, hazardType: ObjectType, padY: Float = 0f, withWarning: Boolean = false) {
            if (withWarning) {
                objects.add(GameObject(x = cursorX - 1.5f, y = padY + 1.2f, type = ObjectType.DECO_WARNING_SIGN))
            }
            objects.add(GameObject(x = cursorX, y = padY, type = padType))
            objects.add(GameObject(x = cursorX + 2.2f, y = 0f, type = hazardType))
            cursorX += 7.5f
        }

        fun addOrbSequence(orbType: ObjectType, orbY: Float = 1.6f, groundSpike: Boolean = true, withPulseDeco: Boolean = false) {
            if (groundSpike) {
                objects.add(GameObject(x = cursorX + 0.8f, y = 0f, type = ObjectType.SPIKE))
            }
            if (withPulseDeco) {
                objects.add(GameObject(x = cursorX + 0.8f, y = orbY + 1.5f, type = ObjectType.DECO_PULSE_RING))
            }
            objects.add(GameObject(x = cursorX + 0.8f, y = orbY, type = orbType))
            cursorX += 6.5f
        }

        // ==========================================
        // DIVERSE SHIP SECTION STYLES
        // ==========================================

        /** 1. CLASSIC OPEN SHIP (Stereo Madness, Blast Processing) - Wide, gentle floating islands & friendly guidance arrows */
        fun addClassicOpenShip(lengthUnits: Float, clearance: Float = 4.6f) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 5f

            val startX = cursorX
            var alt = false
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Gentle floating islands with guiding stars & signs
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

        /** 2. BOUNCY CAVERN SHIP (Back On Track) - Floating pads in air, hanging chains, and crystal formations */
        fun addBouncyCavernShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3.2f, type = ObjectType.DECO_PULSE_RING))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.2f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Ceiling chains hanging down
                if (step % 5 == 0) {
                    objects.add(GameObject(x = cursorX, y = 5.2f, type = ObjectType.DECO_CHAIN))
                }

                // Floating bounce pads & crystal platforms
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

        /** 3. ICE STALACTITE SHIP (Polargeist) - Hanging ceiling icicle spikes & floor crystal stalagmites */
        fun addIceStalactiteShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_CRYSTAL))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                // Ice ceiling and ground
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_OUTLINE))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Alternating hanging stalactites and ground crystal clusters
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

        /** 4. GRAVITY FLIP SHIP (Dry Out, Hexagon Force) - Mid-flight inverting gravity portals! */
        fun addGravityFlipShip(lengthUnits: Float, flipInterval: Float = 18f) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            cursorX += 5f

            val startX = cursorX
            var inverted = false
            var nextFlipAt = startX + 12f

            while (cursorX < startX + lengthUnits) {
                // Ceiling and floor boundaries
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Flip gravity mid-flight
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

                // Floating hazards calibrated for current gravity
                if ((cursorX - startX).toInt() % 14 == 0) {
                    if (inverted) {
                        // Hazard on top while flying inverted
                        objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 4.5f, type = ObjectType.SPIKE_HANGING))
                    } else {
                        // Hazard on bottom
                        objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.SPIKE))
                    }
                }
                cursorX += 2.5f
            }

            // Ensure returning to normal gravity before exit
            if (inverted) {
                objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.PORTAL_GRAVITY_NORMAL))
                cursorX += 4f
            }
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 5. TECH SPEED SHIP (Base After Base, Electroman) - High-tech cyber corridor with laser gate slots */
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

                // Tech gate doorways: wall with a high or low slot opening to navigate through
                if ((cursorX - startX).toInt() % 18 == 0) {
                    objects.add(GameObject(x = cursorX - 2f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
                    if (slotHigh) {
                        // Slot is HIGH (y: 4.2 to 6.5) - Block bottom
                        objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.BLOCK_GRID))
                        objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.SPIKE))
                        objects.add(GameObject(x = cursorX + 1.2f, y = 4.8f, type = ObjectType.DECO_NEON_ARROW))
                    } else {
                        // Slot is LOW (y: 1.2 to 3.5) - Block top
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

        /** 6. JAGGED TEETH SHIP (Can't Let Go) - Claustrophobic cavern with alternating stalagmites & stalactites */
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

                // Tight teeth pillars from top and bottom
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

        /** 7. PLATFORM MAZE SHIP (Jumper) - Multi-tiered floating blocks, pulse rings & foliage */
        fun addPlatformMazeShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_BUSH))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 8 == 0) {
                    objects.add(GameObject(x = cursorX, y = 2.0f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 2.0f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 0.6f, y = 3.2f, type = ObjectType.DECO_PULSE_RING))
                } else if (step % 8 == 4) {
                    objects.add(GameObject(x = cursorX, y = 4.8f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 4.8f, type = ObjectType.BLOCK_GRID))
                    objects.add(GameObject(x = cursorX + 0.6f, y = 3.6f, type = ObjectType.DECO_BUSH))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 8. CHRONO FAST SHIP (Time Machine) - High-tempo ship with speed boosts & neon chevrons */
        fun addChronoFastShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 1.5f, y = 2f, type = ObjectType.PORTAL_SPEED_2X))
            objects.add(GameObject(x = cursorX + 3f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 6f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 7 == 0) {
                    objects.add(GameObject(x = cursorX, y = 3.2f, type = ObjectType.DECO_NEON_ARROW))
                    objects.add(GameObject(x = cursorX + 2f, y = 5.8f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 4f, y = 1.2f, type = ObjectType.SPIKE))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
            objects.add(GameObject(x = cursorX + 2f, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 9. UNDULATING WAVE SHIP (Cycles) - Stair-stepped wave corridors forcing smooth sinusoidal flight */
        fun addUndulatingWaveShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_STAR))
            cursorX += 5f

            val startX = cursorX
            var wavePhase = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Sinusoidal wave guide blocks
                val waveY = when (wavePhase % 4) {
                    0 -> 2.0f
                    1 -> 3.2f
                    2 -> 4.5f
                    else -> 3.2f
                }
                objects.add(GameObject(x = cursorX, y = waveY, type = ObjectType.BLOCK_GRID))
                if (wavePhase % 4 == 2) {
                    objects.add(GameObject(x = cursorX, y = 1.0f, type = ObjectType.DECO_CRYSTAL))
                } else if (wavePhase % 4 == 0) {
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.DECO_STAR))
                }
                wavePhase++
                cursorX += 3.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 10. SAWBLADE GAUNTLET SHIP (xStep, Clutterfunk) - Floating spinning sawblades at alternating altitudes */
        fun addSawbladeGauntletShip(lengthUnits: Float, sawSize: ObjectType = ObjectType.SAWBLADE_MEDIUM) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_WARNING_SIGN))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Floating sawblades
                if (step % 6 == 0) {
                    val sawY = if ((step / 6) % 2 == 0) 4.5f else 1.8f
                    objects.add(GameObject(x = cursorX - 1.5f, y = sawY + 0.2f, type = ObjectType.DECO_WARNING_SIGN))
                    objects.add(GameObject(x = cursorX, y = sawY, type = sawSize))
                    objects.add(GameObject(x = cursorX + 2.5f, y = if (sawY > 3f) 2f else 5f, type = ObjectType.DECO_PULSE_RING))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 11. COSMIC PORTALS SHIP (Theory of Everything) - Gravity inverters, rainbow blocks, glowing stars */
        fun addCosmicPortalsShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3f, type = ObjectType.DECO_STAR))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_RAINBOW))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 5 == 0) {
                    objects.add(GameObject(x = cursorX, y = 4.0f, type = ObjectType.DECO_STAR))
                }

                // Mid-air floating rainbow platforms with crystals
                if (step % 8 == 0) {
                    objects.add(GameObject(x = cursorX, y = 2.5f, type = ObjectType.BLOCK_RAINBOW))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 2.5f, type = ObjectType.BLOCK_RAINBOW))
                    objects.add(GameObject(x = cursorX + 0.6f, y = 3.6f, type = ObjectType.DECO_CRYSTAL))
                    objects.add(GameObject(x = cursorX + 3.0f, y = 5.0f, type = ObjectType.DECO_PULSE_RING))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 12. DEMON MONSTER JAWS SHIP (Clubstep) - Demon jaws with monster eyes, teeth spikes, and throat tunnel! */
        fun addDemonMonsterJawsShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 2f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
            cursorX += 5f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                // Ceiling and ground boundaries
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Giant Monster Jaw Gate!
                if (step % 10 == 0) {
                    // Top monster jaw: Dark blocks + hanging spikes + glowing monster eye!
                    objects.add(GameObject(x = cursorX, y = 6.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 6.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 5.5f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 5.5f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX + 0.6f, y = 7.6f, type = ObjectType.DECO_MONSTER_EYE))

                    // Bottom monster jaw: Dark blocks + upward spikes
                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 0.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                    objects.add(GameObject(x = cursorX + 1.2f, y = 1.5f, type = ObjectType.SPIKE))

                    // Warning sign before throat
                    objects.add(GameObject(x = cursorX - 2.5f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 13. NEON STORM FAST SHIP (Electrodynamix) - High-speed 3x ship rush with strobe hazards */
        fun addNeonStormFastShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 1.5f, y = 2f, type = ObjectType.PORTAL_SPEED_3X))
            objects.add(GameObject(x = cursorX + 3.0f, y = 3f, type = ObjectType.DECO_NEON_ARROW))
            cursorX += 6f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_OUTLINE))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                if (step % 6 == 0) {
                    objects.add(GameObject(x = cursorX, y = 3.2f, type = ObjectType.DECO_TECH_CIRCUIT))
                    objects.add(GameObject(x = cursorX + 1.5f, y = 4.8f, type = ObjectType.SAWBLADE_SMALL))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
            objects.add(GameObject(x = cursorX + 2f, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }

        /** 14. EXTREME DEMON GAUNTLET SHIP (Deadlocked) - The ultimate test with monster jaws and giant saws! */
        fun addExtremeDemonGauntletShip(lengthUnits: Float) {
            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SHIP))
            objects.add(GameObject(x = cursorX + 1.5f, y = 2f, type = ObjectType.PORTAL_SPEED_2X))
            objects.add(GameObject(x = cursorX + 3f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
            cursorX += 6f

            val startX = cursorX
            var step = 0
            while (cursorX < startX + lengthUnits) {
                objects.add(GameObject(x = cursorX, y = 7.5f, type = ObjectType.BLOCK_DARK))
                objects.add(GameObject(x = cursorX, y = 0f, type = ObjectType.SPIKE_SMALL))

                // Monster jaw with monster eye
                if (step % 12 == 0) {
                    objects.add(GameObject(x = cursorX, y = 6.2f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 5.2f, type = ObjectType.SPIKE_HANGING))
                    objects.add(GameObject(x = cursorX, y = 7.6f, type = ObjectType.DECO_MONSTER_EYE))

                    objects.add(GameObject(x = cursorX, y = 0.5f, type = ObjectType.BLOCK_DARK))
                    objects.add(GameObject(x = cursorX, y = 1.5f, type = ObjectType.SPIKE))
                    objects.add(GameObject(x = cursorX - 2f, y = 3.5f, type = ObjectType.DECO_WARNING_SIGN))
                } else if (step % 12 == 6) {
                    // Floating giant saw gauntlet
                    objects.add(GameObject(x = cursorX, y = 4.2f, type = ObjectType.SAWBLADE_LARGE))
                    objects.add(GameObject(x = cursorX + 1.5f, y = 2.0f, type = ObjectType.DECO_PULSE_RING))
                }
                step++
                cursorX += 2.5f
            }

            objects.add(GameObject(x = cursorX, y = 2f, type = ObjectType.PORTAL_SPEED_1X))
            objects.add(GameObject(x = cursorX + 2f, y = 2f, type = ObjectType.PORTAL_CUBE))
            cursorX += 6f
        }
    }

    // ==========================================
    // 18 DISTINCT, POLISHED CAMPAIGN LEVELS
    // ==========================================

    // 1. STEREO MADNESS (Easy 1★) - Classic Open Ship & Guide Deco
    private fun createStereoMadness(): Level {
        val b = LevelBuilder(cursorX = 10f)

        // Intro with neon deco arrows
        b.add(ObjectType.DECO_NEON_ARROW, 1.5f, advanceAfter = 3f)
        for (i in 0..2) {
            b.add(ObjectType.SPIKE, 0f, advanceAfter = 7.5f)
        }

        // Stepped pyramid with Coin 1
        b.add(ObjectType.BLOCK, 0f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 2f)
        b.add(ObjectType.COIN, 3.5f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 1.4f)
        b.add(ObjectType.BLOCK, 0f, advanceAfter = 6f)

        // Yellow pad over spikes
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL, withWarning = true)

        // Bridge platforms with decorative background pillars
        b.add(ObjectType.DECO_PILLAR, 1.5f)
        b.addPlatform(length = 4, y = 1.5f, hasGroundHazard = true, hasDecoChains = true)

        // Color trigger: Cyan shift
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)
        b.add(ObjectType.TRIGGER_GROUND_BLUE, 0.5f)

        // Yellow orbs
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f, withPulseDeco = true)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)

        // Floating pillar with Coin 2
        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.5f)
        b.add(ObjectType.BLOCK, 3f)
        b.add(ObjectType.COIN, 4.5f, advanceAfter = 4f)
        b.add(ObjectType.BLOCK, 1f, advanceAfter = 5f)

        // DISTINCT SHIP: Classic Open Ship Flight with gentle islands & guiding stars
        b.addClassicOpenShip(lengthUnits = 45f)

        // Color trigger: Purple shift
        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 4f)
        b.add(ObjectType.TRIGGER_GROUND_PURPLE, 0.5f)

        // Sprint to finish line with Coin 3
        for (i in 0..1) {
            b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        }
        b.addPlatform(length = 5, y = 2f, hasGroundHazard = true)
        b.add(ObjectType.COIN, 3.8f)
        b.advance(4f)
        b.add(ObjectType.BLOCK_RAINBOW, 0f, advanceAfter = 2f)
        b.add(ObjectType.BLOCK_RAINBOW, 1f, advanceAfter = 2f)
        b.add(ObjectType.BLOCK_RAINBOW, 2f, advanceAfter = 8f)
        b.advance(15f)

        return Level(
            id = "main_stereo_madness",
            name = "Stereo Madness",
            description = "The classic original journey. Accessible timing, blocks, guide arrows, and open ship flight.",
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

    // 2. BACK ON TRACK (Easy 2★) - Bouncy Cavern Ship with mid-air bounce pads & hanging chains
    private fun createBackOnTrack(): Level {
        val b = LevelBuilder(cursorX = 10f)

        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL, withWarning = true)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)

        b.add(ObjectType.PAD_YELLOW, 0f, advanceAfter = 2.4f)
        b.add(ObjectType.BLOCK, 2f)
        b.add(ObjectType.COIN, 3.6f, advanceAfter = 1.2f)
        b.add(ObjectType.PAD_YELLOW, 2f, advanceAfter = 5.5f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.add(ObjectType.DECO_NEON_ARROW, 1.5f, advanceAfter = 4f)

        // DISTINCT SHIP: Bouncy Cavern Ship
        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 2f)
        b.addBouncyCavernShip(lengthUnits = 50f)

        // Climax with Coin 2 & 3
        b.add(ObjectType.PAD_PINK, 0f, advanceAfter = 2.2f)
        b.add(ObjectType.BLOCK, 1.2f)
        b.add(ObjectType.COIN, 2.6f, advanceAfter = 3.5f)
        b.addPlatform(length = 6, y = 1.5f, hasGroundHazard = true, hasDecoChains = true)
        b.add(ObjectType.COIN, 4f)
        b.advance(18f)

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

    // 3. POLARGEIST (Normal 3★) - Ice Stalactite Ship with hanging icicle spikes & crystals
    private fun createPolargeist(): Level {
        val b = LevelBuilder(cursorX = 10f)

        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2.2f, withPulseDeco = true)
        b.add(ObjectType.ORB_YELLOW, 1.6f, advanceAfter = 2.2f)
        b.add(ObjectType.COIN, 3.8f, advanceAfter = 4f)

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.addPlatform(length = 5, y = 1.5f, hasGroundHazard = true)
        b.add(ObjectType.DECO_CRYSTAL, 1.5f, advanceAfter = 4f)

        // DISTINCT SHIP: Ice Stalactite Ship
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 2f)
        b.addIceStalactiteShip(lengthUnits = 55f)

        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f)
        b.addPlatform(length = 6, y = 1.5f)
        b.advance(18f)

        return Level(
            id = "main_polargeist",
            name = "Polargeist",
            description = "Ice cavern atmosphere! Mid-air jump rings and hanging stalactite ship navigation.",
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

    // 4. DRY OUT (Normal 3★) - Mid-Flight Inverting Gravity Portals Ship!
    private fun createDryOut(): Level {
        val b = LevelBuilder(cursorX = 10f)

        b.addPlatform(length = 4, y = 1f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)

        // Inverted gravity cube sequence
        b.add(ObjectType.PORTAL_GRAVITY_INVERT, 1.5f, advanceAfter = 5f)
        for (i in 0..4) {
            b.add(ObjectType.BLOCK_DARK, 6.5f)
            b.add(ObjectType.SPIKE_HANGING, 5.5f)
            b.advance(1.8f)
        }
        b.add(ObjectType.COIN, 4.5f)
        b.advance(4f)
        b.add(ObjectType.PORTAL_GRAVITY_NORMAL, 5.5f, advanceAfter = 6f)

        // DISTINCT SHIP: Gravity Flip Ship (Inverting gravity mid-flight!)
        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addGravityFlipShip(lengthUnits = 55f, flipInterval = 18f)

        b.add(ObjectType.COIN, 2.5f)
        b.addPlatform(length = 6, y = 1.2f)
        b.advance(18f)

        return Level(
            id = "main_dry_out",
            name = "Dry Out",
            description = "Upside-down gravity flips! Experience inverted flight in the ship part and roof platforming.",
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

    // 5. BASE AFTER BASE (Normal 4★) - Tech Speed Ship with laser barrier gates
    private fun createBaseAfterBase(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 4, y = 1.5f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 1.8f)
        b.add(ObjectType.COIN, 3.5f)
        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 4f)

        // DISTINCT SHIP: Tech Speed Ship (2x speed with laser barrier slot doors)
        b.addTechSpeedShip(lengthUnits = 60f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 3.8f)
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)

        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_TRIPLE, withWarning = true)
        b.addOrbSequence(ObjectType.ORB_PINK, orbY = 1.6f)
        b.add(ObjectType.COIN, 3.2f)
        b.addPlatform(length = 7, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_base_after_base",
            name = "Base After Base",
            description = "High-tech cyber factory! 2x speed ship passage through alternating laser barrier gates.",
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

    // 6. CANT LET GO (Normal 4★) - Jagged Teeth Ship with alternating stalagmites & chains
    private fun createCantLetGo(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 5, y = 1.2f, hasGroundHazard = true, hasDecoChains = true)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 7.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_PURPLE, ObjectType.SPIKE)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 4f)
        // DISTINCT SHIP: Jagged Teeth Ship
        b.addJaggedTeethShip(lengthUnits = 60f)
        b.add(ObjectType.COIN, 4f)

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_cant_let_go",
            name = "Can't Let Go",
            description = "Dark jagged cavern with claustrophobic ceiling spikes, hanging chains, and tight teeth ship flight.",
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

    // 7. JUMPER (Hard 5★) - Platform Maze Ship with multi-tiered blocks & foliage
    private fun createJumper(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.addJumpPadOverHazard(ObjectType.PAD_PINK, ObjectType.SPIKE)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f, withPulseDeco = true)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 4f)
        // DISTINCT SHIP: Platform Maze Ship
        b.addPlatformMazeShip(lengthUnits = 65f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 4f)
        b.addJumpPadOverHazard(ObjectType.PAD_RED, ObjectType.SPIKE_TRIPLE, withWarning = true)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_jumper",
            name = "Jumper",
            description = "High bouncing energetic pads with vertical platform maze ship section and neon pulse decorations.",
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

    // 8. TIME MACHINE (Hard 5★) - Chrono Fast Ship with 2x speed & neon chevrons
    private fun createTimeMachine(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 5, y = 1.2f)
        b.add(ObjectType.SPIKE_DUAL, 0f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Chrono Fast Ship
        b.addChronoFastShip(lengthUnits = 60f)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.PORTAL_SPEED_1X, 1f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 2f)
        b.addPlatform(length = 7, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_time_machine",
            name = "Time Machine",
            description = "Speed bursts across neon dimensions! Fast-paced ship section with precision timing.",
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

    // 9. CYCLES (Hard 6★) - Undulating Wave Ship with sloped stair-step corridors
    private fun createCycles(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 4, y = 1.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SAWBLADE_MEDIUM, withWarning = true)
        b.add(ObjectType.COIN, 3.6f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Undulating Wave Ship
        b.addUndulatingWaveShip(lengthUnits = 70f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_cycles",
            name = "Cycles",
            description = "Spinning sawblades and sinusoidal wave flight requiring smooth continuous throttle control.",
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

    // 10. XSTEP (Hard 6★) - Sawblade Gauntlet Ship with floating spinning saws
    private fun createXStep(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addOrbSequence(ObjectType.ORB_BLUE, orbY = 1.8f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Sawblade Gauntlet Ship
        b.addSawbladeGauntletShip(lengthUnits = 70f, sawSize = ObjectType.SAWBLADE_MEDIUM)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 6, y = 2f)
        b.advance(20f)

        return Level(
            id = "main_xstep",
            name = "xStep",
            description = "Blue gravity rings, breakable rhythm blocks, and a high-tension floating sawblade gauntlet.",
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

    // 11. CLUTTERFUNK (Harder 7★) - High-Hazard Sawblade Gauntlet with giant saws
    private fun createClutterfunk(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.SAWBLADE_GIANT, 2f, advanceAfter = 8f)
        b.addPlatform(length = 5, y = 1.5f)
        b.add(ObjectType.COIN, 3.6f)

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Sawblade Gauntlet with tighter clearance
        b.addSawbladeGauntletShip(lengthUnits = 75f, sawSize = ObjectType.SAWBLADE_LARGE)
        b.add(ObjectType.COIN, 4f)

        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_RED, orbY = 2.2f)
        b.addPlatform(length = 7, y = 2f)
        b.advance(20f)

        return Level(
            id = "main_clutterfunk",
            name = "Clutterfunk",
            description = "Industrial chaos! Giant spinning saws and high-density obstacle navigation.",
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

    // 12. THEORY OF EVERYTHING (Harder 8★) - Cosmic Portals Ship with rainbow blocks & stars
    private fun createTheoryOfEverything(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 6, y = 1.5f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f, withPulseDeco = true)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Cosmic Portals Ship
        b.addCosmicPortalsShip(lengthUnits = 80f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.2f)
        b.addPlatform(length = 8, y = 1.8f)
        b.advance(20f)

        return Level(
            id = "main_theory_of_everything",
            name = "Theory of Everything",
            description = "Cosmic wonder of geometry! Starlit rainbow islands, cosmic ship flight, and sublime beats.",
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

    // 13. ELECTROMAN (Harder 8★) - High-voltage Tech Speed Ship
    private fun createElectroman(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Tech Speed Ship
        b.addTechSpeedShip(lengthUnits = 80f, speed = ObjectType.PORTAL_SPEED_2X)
        b.add(ObjectType.COIN, 4f)

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_electroman",
            name = "Electroman Adventures",
            description = "High-voltage electro synth beats with rapid laser gate navigation in high-speed flight.",
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

    // 14. CLUBSTEP (Insane 9★) - Demon Monster Jaws Ship with monster eyes & throat tunnel!
    private fun createClubstep(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 2f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.SAWBLADE_GIANT, 2.5f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 3.8f)

        // DISTINCT SHIP: The Legendary Demon Monster Jaws Ship!
        b.addDemonMonsterJawsShip(lengthUnits = 85f)
        b.add(ObjectType.COIN, 4f)

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2f)
        b.addPlatform(length = 8, y = 2f)
        b.advance(20f)

        return Level(
            id = "main_clubstep",
            name = "Clubstep",
            description = "The original Demon challenge! Demon face jaws with glowing monster eyes and infernal throats.",
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

    // 15. ELECTRODYNAMIX (Insane 9★) - Neon Storm Fast Ship with 3x speed bursts
    private fun createElectrodynamix(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.PORTAL_SPEED_3X, 1f, advanceAfter = 4f)
        b.addPlatform(length = 6, y = 1.5f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Neon Storm Fast Ship
        b.addNeonStormFastShip(lengthUnits = 90f)
        b.add(ObjectType.COIN, 4f)

        b.add(ObjectType.TRIGGER_BG_PURPLE, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
        b.advance(20f)

        return Level(
            id = "main_electrodynamix",
            name = "Electrodynamix",
            description = "Insane 3x speed bursts and razor-sharp ship navigation through neon strobe storms.",
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

    // 16. HEXAGON FORCE (Insane 9★) - Dual Gravity Flip Ship with hexagonal pillars
    private fun createHexagonForce(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.addPlatform(length = 6, y = 1.5f)
        b.addOrbSequence(ObjectType.ORB_GREEN, orbY = 2f)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Gravity Flip Ship
        b.addGravityFlipShip(lengthUnits = 90f, flipInterval = 20f)
        b.add(ObjectType.COIN, 3.5f)

        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_DASH, orbY = 2f)
        b.addPlatform(length = 8, y = 1.8f)
        b.advance(20f)

        return Level(
            id = "main_hexagon_force",
            name = "Hexagon Force",
            description = "Dual portals, alternating gravity flips in flight, and intricate geometric grids.",
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

    // 17. BLAST PROCESSING (Normal 4★) - Classic Open Ship with wide geometric flow
    private fun createBlastProcessing(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_CYAN, 0.5f, advanceAfter = 2f)
        b.addPlatform(length = 7, y = 1.5f)
        b.addJumpPadOverHazard(ObjectType.PAD_YELLOW, ObjectType.SPIKE_DUAL)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_GREEN, 0.5f, advanceAfter = 3f)
        // DISTINCT SHIP: Spacious Open Ship
        b.addClassicOpenShip(lengthUnits = 90f, clearance = 4.8f)
        b.add(ObjectType.COIN, 3.6f)

        b.add(ObjectType.TRIGGER_BG_ORANGE, 0.5f, advanceAfter = 3f)
        b.addOrbSequence(ObjectType.ORB_YELLOW, orbY = 2f)
        b.addPlatform(length = 8, y = 1.5f)
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

    // 18. DEADLOCKED (Demon 10★) - Extreme Demon Gauntlet Ship with monster jaws & giant saws!
    private fun createDeadlocked(): Level {
        val b = LevelBuilder(cursorX = 10f)
        b.add(ObjectType.TRIGGER_BG_RED, 0.5f, advanceAfter = 2f)
        b.add(ObjectType.PORTAL_SPEED_3X, 1f, advanceAfter = 4f)
        b.add(ObjectType.SAWBLADE_GIANT, 2f, advanceAfter = 8f)
        b.add(ObjectType.COIN, 4f)

        // DISTINCT SHIP: Extreme Demon Gauntlet Ship
        b.addExtremeDemonGauntletShip(lengthUnits = 95f)
        b.add(ObjectType.COIN, 3.8f)

        b.add(ObjectType.TRIGGER_BG_DARK, 0.5f, advanceAfter = 3f)
        b.add(ObjectType.PORTAL_SPEED_2X, 1f, advanceAfter = 4f)
        b.addOrbSequence(ObjectType.ORB_BLACK, orbY = 2.2f)
        b.addOrbSequence(ObjectType.ORB_RAINBOW, orbY = 2.4f)
        b.addPlatform(length = 9, y = 2f)
        b.advance(20f)

        return Level(
            id = "main_deadlocked",
            name = "Deadlocked",
            description = "The ultimate Demon showdown! Demon face jaws with monster eyes, giant spinning saws, and rapid speed shifts.",
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
