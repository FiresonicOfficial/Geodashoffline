package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Difficulty
import com.example.model.Level

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val difficulty: String,
    val stars: Int,
    val author: String,
    val isCustom: Boolean,
    val musicTrack: Int,
    val bgColor: Long,
    val groundColor: Long,
    val objectsJson: String,
    val bestPercentage: Int = 0,
    val completed: Boolean = false,
    val attempts: Int = 0,
    val jumps: Int = 0,
    val coinsCollected: Int = 0,
    val userRating: Float = 0f,
    val userRatingsCount: Int = 0,
    val likesCount: Int = 0,
    val isUnlocked: Boolean = true,
    val highScore: Long = 0L,
    val unlockRequirement: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Level {
        return Level(
            id = id,
            name = name,
            description = description,
            difficulty = Difficulty.fromString(difficulty),
            stars = stars,
            author = author,
            isCustom = isCustom,
            musicTrack = musicTrack,
            bgColor = bgColor,
            groundColor = groundColor,
            objects = Level.deserializeObjects(objectsJson),
            bestPercentage = bestPercentage,
            completed = completed,
            attempts = attempts,
            jumps = jumps,
            coinsCollected = coinsCollected,
            userRating = userRating,
            userRatingsCount = userRatingsCount,
            likesCount = likesCount,
            isUnlocked = isUnlocked,
            highScore = highScore,
            unlockRequirement = unlockRequirement,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomainModel(level: Level): LevelEntity {
            return LevelEntity(
                id = level.id,
                name = level.name,
                description = level.description,
                difficulty = level.difficulty.name,
                stars = level.stars,
                author = level.author,
                isCustom = level.isCustom,
                musicTrack = level.musicTrack,
                bgColor = level.bgColor,
                groundColor = level.groundColor,
                objectsJson = Level.serializeObjects(level.objects),
                bestPercentage = level.bestPercentage,
                completed = level.completed,
                attempts = level.attempts,
                jumps = level.jumps,
                coinsCollected = level.coinsCollected,
                userRating = level.userRating,
                userRatingsCount = level.userRatingsCount,
                likesCount = level.likesCount,
                isUnlocked = level.isUnlocked,
                highScore = level.highScore,
                unlockRequirement = level.unlockRequirement,
                createdAt = level.createdAt
            )
        }
    }
}
