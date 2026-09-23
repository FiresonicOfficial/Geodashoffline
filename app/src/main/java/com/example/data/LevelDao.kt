package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels ORDER BY createdAt ASC")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE isCustom = 0 ORDER BY createdAt ASC")
    fun getMainLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE isCustom = 1 ORDER BY createdAt DESC")
    fun getCustomLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE id = :id LIMIT 1")
    suspend fun getLevelById(id: String): LevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(level: LevelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(levels: List<LevelEntity>)

    @Update
    suspend fun update(level: LevelEntity)

    @Query("DELETE FROM levels WHERE id = :id")
    suspend fun deleteLevel(id: String)

    @Query("""
        UPDATE levels 
        SET bestPercentage = CASE WHEN :newPercentage > bestPercentage THEN :newPercentage ELSE bestPercentage END,
            highScore = CASE WHEN :newHighScore > highScore THEN :newHighScore ELSE highScore END,
            completed = CASE WHEN :completed = 1 OR completed = 1 THEN 1 ELSE 0 END,
            attempts = attempts + :additionalAttempts,
            jumps = jumps + :additionalJumps,
            coinsCollected = CASE WHEN :newCoins > coinsCollected THEN :newCoins ELSE coinsCollected END
        WHERE id = :id
    """)
    suspend fun recordAttempt(
        id: String,
        newPercentage: Int,
        newHighScore: Long,
        completed: Boolean,
        additionalAttempts: Int,
        additionalJumps: Int,
        newCoins: Int
    )

    @Query("UPDATE levels SET isUnlocked = 1 WHERE id = :id")
    suspend fun unlockLevel(id: String)

    @Query("SELECT * FROM levels WHERE isUnlocked = 1")
    fun getUnlockedLevels(): Flow<List<LevelEntity>>

    @Query("""
        UPDATE levels 
        SET userRating = :newRating,
            userRatingsCount = :ratingsCount,
            likesCount = :likesCount
        WHERE id = :id
    """)
    suspend fun submitRating(
        id: String,
        newRating: Float,
        ratingsCount: Int,
        likesCount: Int
    )
}
