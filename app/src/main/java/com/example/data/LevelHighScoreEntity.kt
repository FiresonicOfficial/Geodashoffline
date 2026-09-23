package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_high_scores")
data class LevelHighScoreEntity(
    @PrimaryKey
    val levelId: String,
    val levelName: String,
    val highScorePoints: Long = 0L,
    val bestPercentage: Int = 0,
    val completed: Boolean = false,
    val coinsCollected: Int = 0,
    val attempts: Int = 0,
    val jumps: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockRequirement: String = "",
    val lastPlayedTimestamp: Long = System.currentTimeMillis()
)
