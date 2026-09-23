package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProgressDao {

    @Query("SELECT * FROM player_progress WHERE id = :id LIMIT 1")
    fun getPlayerProgressFlow(id: String = "primary_player"): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = :id LIMIT 1")
    suspend fun getPlayerProgress(id: String = "primary_player"): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: PlayerProgressEntity)

    @Query("""
        UPDATE player_progress
        SET resumeLevelId = :levelId,
            resumeLevelName = :levelName,
            resumePercentage = CASE WHEN :newPercentage > resumePercentage AND resumeLevelId = :levelId THEN :newPercentage ELSE :newPercentage END,
            resumeHighScore = CASE WHEN :highScore > resumeHighScore AND resumeLevelId = :levelId THEN :highScore ELSE :highScore END,
            resumeIsPractice = :isPractice,
            lastUpdated = :timestamp
        WHERE id = 'primary_player'
    """)
    suspend fun updateResumePoint(
        levelId: String,
        levelName: String,
        newPercentage: Int,
        highScore: Long,
        isPractice: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )

    // High Scores table queries
    @Query("SELECT * FROM level_high_scores ORDER BY highScorePoints DESC")
    fun getAllHighScores(): Flow<List<LevelHighScoreEntity>>

    @Query("SELECT * FROM level_high_scores WHERE levelId = :levelId LIMIT 1")
    suspend fun getHighScoreForLevel(levelId: String): LevelHighScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHighScore(highScore: LevelHighScoreEntity)

    @Query("""
        UPDATE level_high_scores
        SET highScorePoints = CASE WHEN :scorePoints > highScorePoints THEN :scorePoints ELSE highScorePoints END,
            bestPercentage = CASE WHEN :percentage > bestPercentage THEN :percentage ELSE bestPercentage END,
            completed = CASE WHEN :completed = 1 OR completed = 1 THEN 1 ELSE 0 END,
            coinsCollected = CASE WHEN :coins > coinsCollected THEN :coins ELSE coinsCollected END,
            attempts = attempts + :additionalAttempts,
            jumps = jumps + :additionalJumps,
            lastPlayedTimestamp = :timestamp
        WHERE levelId = :levelId
    """)
    suspend fun recordLevelResult(
        levelId: String,
        scorePoints: Long,
        percentage: Int,
        completed: Boolean,
        coins: Int,
        additionalAttempts: Int,
        additionalJumps: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE level_high_scores SET isUnlocked = 1 WHERE levelId = :levelId")
    suspend fun unlockLevelHighScore(levelId: String)
}
