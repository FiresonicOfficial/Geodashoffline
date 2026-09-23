package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey
    val id: String = "primary_player",
    val resumeLevelId: String? = "main_stereo_madness",
    val resumeLevelName: String? = "Stereo Madness",
    val resumePercentage: Int = 0,
    val resumeHighScore: Long = 0L,
    val resumeIsPractice: Boolean = false,
    val currentCampaignLevelId: String = "main_stereo_madness",
    val unlockedLevelIdsJson: String = "[\"main_stereo_madness\"]",
    val totalStars: Int = 0,
    val totalCoins: Int = 0,
    val demonsCompleted: Int = 0,
    val totalAttempts: Int = 0,
    val totalJumps: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun getUnlockedLevelIds(): Set<String> {
        return try {
            val arr = JSONArray(unlockedLevelIdsJson)
            val set = mutableSetOf<String>()
            for (i in 0 until arr.length()) {
                set.add(arr.getString(i))
            }
            set
        } catch (e: Exception) {
            setOf("main_stereo_madness")
        }
    }

    companion object {
        fun createDefault(): PlayerProgressEntity {
            return PlayerProgressEntity(
                id = "primary_player",
                resumeLevelId = "main_stereo_madness",
                resumeLevelName = "Stereo Madness",
                resumePercentage = 0,
                resumeHighScore = 0L,
                resumeIsPractice = false,
                currentCampaignLevelId = "main_stereo_madness",
                unlockedLevelIdsJson = "[\"main_stereo_madness\"]"
            )
        }

        fun serializeUnlockedIds(ids: Collection<String>): String {
            val arr = JSONArray()
            for (id in ids) {
                arr.put(id)
            }
            return arr.toString()
        }
    }
}
