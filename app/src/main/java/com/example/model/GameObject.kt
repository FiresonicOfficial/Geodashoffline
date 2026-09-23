package com.example.model

enum class ObjectCategory(val displayName: String) {
    BLOCKS("Blocks"),
    HAZARDS("Hazards"),
    PADS("Pads"),
    ORBS("Orbs"),
    PORTALS("Portals"),
    SPECIAL("Special")
}

enum class ObjectType(
    val displayName: String,
    val category: ObjectCategory,
    val width: Float = 1f,
    val height: Float = 1f,
    val primaryColorHex: Long = 0xFF00E5FF,
    val isSolid: Boolean = false,
    val isLethal: Boolean = false,
    val isInteractive: Boolean = false
) {
    // Blocks
    BLOCK("Cube Block", ObjectCategory.BLOCKS, 1f, 1f, 0xFF00E5FF, isSolid = true),
    BLOCK_DARK("Dark Block", ObjectCategory.BLOCKS, 1f, 1f, 0xFF37474F, isSolid = true),
    BLOCK_GRID("Grid Block", ObjectCategory.BLOCKS, 1f, 1f, 0xFF7C4DFF, isSolid = true),
    BLOCK_OUTLINE("Neon Outline", ObjectCategory.BLOCKS, 1f, 1f, 0xFF00E5FF, isSolid = true),
    HALF_BLOCK("Half Slab", ObjectCategory.BLOCKS, 1f, 0.5f, 0xFF00B0FF, isSolid = true),

    // Hazards
    SPIKE("Spike", ObjectCategory.HAZARDS, 1f, 1f, 0xFFFF1744, isLethal = true),
    SPIKE_SMALL("Small Spike", ObjectCategory.HAZARDS, 1f, 0.5f, 0xFFFF5252, isLethal = true),
    SPIKE_HANGING("Ceiling Spike", ObjectCategory.HAZARDS, 1f, 1f, 0xFFFF1744, isLethal = true),
    SPIKE_DUAL("Dual Spikes", ObjectCategory.HAZARDS, 1f, 0.7f, 0xFFFF3D00, isLethal = true),
    SPIKE_TRIPLE("Triple Spikes", ObjectCategory.HAZARDS, 3f, 1f, 0xFFFF1744, isLethal = true),
    SPIKE_FOUR("Quad Spikes", ObjectCategory.HAZARDS, 4f, 1f, 0xFFFF1744, isLethal = true),
    SAWBLADE_LARGE("Sawblade (Large)", ObjectCategory.HAZARDS, 2f, 2f, 0xFFFF5722, isLethal = true),
    SAWBLADE_MEDIUM("Sawblade (Med)", ObjectCategory.HAZARDS, 1.5f, 1.5f, 0xFFFF5722, isLethal = true),
    SAWBLADE_SMALL("Sawblade (Small)", ObjectCategory.HAZARDS, 1f, 1f, 0xFFFF5722, isLethal = true),

    // Jump Pads (instant impulse on contact)
    PAD_YELLOW("Yellow Pad", ObjectCategory.PADS, 1f, 0.25f, 0xFFFFD600, isInteractive = true),
    PAD_PINK("Pink Pad", ObjectCategory.PADS, 1f, 0.25f, 0xFFFF4081, isInteractive = true),
    PAD_RED("Red Pad (Mega)", ObjectCategory.PADS, 1f, 0.25f, 0xFFFF1744, isInteractive = true),
    PAD_GRAVITY("Gravity Pad", ObjectCategory.PADS, 1f, 0.25f, 0xFF00E5FF, isInteractive = true),

    // Jump Rings / Orbs (impulse when tapped while inside trigger radius)
    ORB_YELLOW("Yellow Orb", ObjectCategory.ORBS, 1f, 1f, 0xFFFFD600, isInteractive = true),
    ORB_PINK("Pink Orb", ObjectCategory.ORBS, 1f, 1f, 0xFFFF4081, isInteractive = true),
    ORB_RED("Red Orb (Mega)", ObjectCategory.ORBS, 1f, 1f, 0xFFFF1744, isInteractive = true),
    ORB_BLUE("Blue Orb", ObjectCategory.ORBS, 1f, 1f, 0xFF00B0FF, isInteractive = true),
    ORB_GREEN("Green Orb", ObjectCategory.ORBS, 1f, 1f, 0xFF00E676, isInteractive = true),
    ORB_BLACK("Black Slam Orb", ObjectCategory.ORBS, 1f, 1f, 0xFF7C4DFF, isInteractive = true),
    ORB_DASH("Green Dash Orb", ObjectCategory.ORBS, 1f, 1f, 0xFF00E676, isInteractive = true),

    // Portals
    PORTAL_SHIP("Ship Portal", ObjectCategory.PORTALS, 1f, 2.5f, 0xFFFF4081, isInteractive = true),
    PORTAL_CUBE("Cube Portal", ObjectCategory.PORTALS, 1f, 2.5f, 0xFF00E676, isInteractive = true),
    PORTAL_GRAVITY_INVERT("Gravity Invert", ObjectCategory.PORTALS, 1f, 2.5f, 0xFFFF9100, isInteractive = true),
    PORTAL_GRAVITY_NORMAL("Gravity Normal", ObjectCategory.PORTALS, 1f, 2.5f, 0xFF2979FF, isInteractive = true),
    PORTAL_SPEED_0_5X("Speed 0.5x", ObjectCategory.PORTALS, 1f, 2.5f, 0xFFFFAB00, isInteractive = true),
    PORTAL_SPEED_1X("Speed 1x", ObjectCategory.PORTALS, 1f, 2.5f, 0xFF00E676, isInteractive = true),
    PORTAL_SPEED_2X("Speed 2x", ObjectCategory.PORTALS, 1f, 2.5f, 0xFF00E5FF, isInteractive = true),
    PORTAL_SPEED_3X("Speed 3x", ObjectCategory.PORTALS, 1f, 2.5f, 0xFFFF1744, isInteractive = true),
    PORTAL_SPEED_4X("Speed 4x", ObjectCategory.PORTALS, 1f, 2.5f, 0xFFD500F9, isInteractive = true),

    // Collectibles & Special
    COIN("Secret Coin", ObjectCategory.SPECIAL, 1f, 1f, 0xFFFFD700, isInteractive = true)
}

data class GameObject(
    val x: Float,
    val y: Float,
    val type: ObjectType,
    val id: Long = System.nanoTime()
)
