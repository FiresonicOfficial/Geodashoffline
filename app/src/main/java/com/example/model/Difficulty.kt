package com.example.model

enum class Difficulty(
    val displayName: String,
    val defaultStars: Int,
    val colorHex: Long,
    val iconName: String
) {
    AUTO("Auto", 1, 0xFF00E5FF, "AUTO"),
    EASY("Easy", 2, 0xFF2979FF, "EASY"),
    NORMAL("Normal", 3, 0xFF00E676, "NORMAL"),
    HARD("Hard", 4, 0xFFFFD600, "HARD"),
    HARDER("Harder", 6, 0xFFFF9100, "HARDER"),
    INSANE("Insane", 8, 0xFFFF1744, "INSANE"),
    DEMON("Demon", 10, 0xFFD500F9, "DEMON");

    companion object {
        fun fromString(value: String): Difficulty {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                NORMAL
            }
        }
    }
}
