package com.example.data

import com.example.model.Difficulty
import com.example.model.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LevelRepository(
    private val levelDao: LevelDao,
    private val playerProgressDao: PlayerProgressDao
) {

    init {
        // Seed initial campaign levels and player progress if database is new
        CoroutineScope(Dispatchers.IO).launch {
            val defaults = DefaultLevels.getAllDefaultLevels()
            val entities = defaults.map { LevelEntity.fromDomainModel(it) }
            levelDao.insertAll(entities)

            // Seed initial player progress if empty
            val currentProgress = playerProgressDao.getPlayerProgress()
            if (currentProgress == null) {
                playerProgressDao.insertOrUpdateProgress(PlayerProgressEntity.createDefault())
            }

            // Seed initial high score entries
            for (def in defaults) {
                val existing = playerProgressDao.getHighScoreForLevel(def.id)
                if (existing == null) {
                    playerProgressDao.insertOrUpdateHighScore(
                        LevelHighScoreEntity(
                            levelId = def.id,
                            levelName = def.name,
                            highScorePoints = 0L,
                            bestPercentage = 0,
                            completed = false,
                            coinsCollected = 0,
                            attempts = 0,
                            jumps = 0,
                            isUnlocked = def.isUnlocked,
                            unlockRequirement = def.unlockRequirement
                        )
                    )
                }
            }
        }
    }

    val mainLevels: Flow<List<Level>> = levelDao.getMainLevels().map { list ->
        list.map { it.toDomainModel() }
    }

    val customLevels: Flow<List<Level>> = levelDao.getCustomLevels().map { list ->
        list.map { it.toDomainModel() }
    }

    val allLevels: Flow<List<Level>> = levelDao.getAllLevels().map { list ->
        list.map { it.toDomainModel() }
    }

    val playerProgress: Flow<PlayerProgressEntity?> = playerProgressDao.getPlayerProgressFlow()

    val highScores: Flow<List<LevelHighScoreEntity>> = playerProgressDao.getAllHighScores()

    suspend fun getLevelById(id: String): Level? {
        return levelDao.getLevelById(id)?.toDomainModel()
    }

    suspend fun saveCustomLevel(level: Level) {
        val entity = LevelEntity.fromDomainModel(level.copy(isCustom = true, isUnlocked = true))
        levelDao.insertOrUpdate(entity)
    }

    suspend fun deleteLevel(id: String) {
        levelDao.deleteLevel(id)
    }

    suspend fun recordAttempt(
        id: String,
        newPercentage: Int,
        completed: Boolean,
        additionalAttempts: Int,
        additionalJumps: Int,
        newCoins: Int,
        isPractice: Boolean = false
    ) {
        val currentLevel = levelDao.getLevelById(id)
        val levelName = currentLevel?.name ?: "Level"
        val isDemon = currentLevel?.difficulty == Difficulty.DEMON.name

        // High score calculation: base points from distance + coins + completion bonus
        val points = (newPercentage * 100L) + (newCoins * 2500L) +
            (if (completed) 10000L else 0L) +
            (if (completed && isDemon) 25000L else 0L)

        // 1. Update level entity
        levelDao.recordAttempt(
            id = id,
            newPercentage = newPercentage,
            newHighScore = points,
            completed = completed,
            additionalAttempts = additionalAttempts,
            additionalJumps = additionalJumps,
            newCoins = newCoins
        )

        // 2. Update level high score table
        playerProgressDao.recordLevelResult(
            levelId = id,
            scorePoints = points,
            percentage = newPercentage,
            completed = completed,
            coins = newCoins,
            additionalAttempts = additionalAttempts,
            additionalJumps = additionalJumps
        )

        // 3. Evaluate Campaign Progression unlocks
        checkAndUnlockCampaignLevels(id, newPercentage, completed)

        // 4. Update Player Progress & Campaign Resume State
        updatePlayerResumeAndStats(id, levelName, newPercentage, points, isPractice)
    }

    private suspend fun checkAndUnlockCampaignLevels(
        lastPlayedId: String,
        percentage: Int,
        completed: Boolean
    ) {
        val levels = levelDao.getLevelById("main_stereo_madness") ?: return

        // Fetch all current main levels to inspect progress
        val allMain = listOfNotNull(
            levelDao.getLevelById("main_stereo_madness"),
            levelDao.getLevelById("main_back_on_track"),
            levelDao.getLevelById("main_polargeist"),
            levelDao.getLevelById("main_dry_out"),
            levelDao.getLevelById("main_base_after_base"),
            levelDao.getLevelById("main_clubstep")
        )

        val totalStars = allMain.filter { it.completed }.sumOf { it.stars }
        val unlockedIds = mutableSetOf<String>()
        unlockedIds.add("main_stereo_madness") // Always unlocked

        val stereo = allMain.find { it.id == "main_stereo_madness" }
        val backOnTrack = allMain.find { it.id == "main_back_on_track" }
        val polargeist = allMain.find { it.id == "main_polargeist" }
        val dryOut = allMain.find { it.id == "main_dry_out" }
        val baseAfterBase = allMain.find { it.id == "main_base_after_base" }

        // Unlock Back On Track
        if ((stereo?.bestPercentage ?: 0) >= 50 || (stereo?.completed == true) || totalStars >= 1) {
            unlockedIds.add("main_back_on_track")
            levelDao.unlockLevel("main_back_on_track")
            playerProgressDao.unlockLevelHighScore("main_back_on_track")
        }

        // Unlock Polargeist
        if ((backOnTrack?.bestPercentage ?: 0) >= 50 || (backOnTrack?.completed == true) || totalStars >= 3) {
            unlockedIds.add("main_polargeist")
            levelDao.unlockLevel("main_polargeist")
            playerProgressDao.unlockLevelHighScore("main_polargeist")
        }

        // Unlock Dry Out
        if ((polargeist?.bestPercentage ?: 0) >= 50 || (polargeist?.completed == true) || totalStars >= 6) {
            unlockedIds.add("main_dry_out")
            levelDao.unlockLevel("main_dry_out")
            playerProgressDao.unlockLevelHighScore("main_dry_out")
        }

        // Unlock Base After Base
        if ((dryOut?.bestPercentage ?: 0) >= 50 || (dryOut?.completed == true) || totalStars >= 10) {
            unlockedIds.add("main_base_after_base")
            levelDao.unlockLevel("main_base_after_base")
            playerProgressDao.unlockLevelHighScore("main_base_after_base")
        }

        // Unlock Clubstep (Demon)
        if ((baseAfterBase?.completed == true) || (baseAfterBase?.bestPercentage ?: 0) >= 75 || totalStars >= 14) {
            unlockedIds.add("main_clubstep")
            levelDao.unlockLevel("main_clubstep")
            playerProgressDao.unlockLevelHighScore("main_clubstep")
        }

        // Persist unlocked list in player progress
        val current = playerProgressDao.getPlayerProgress() ?: PlayerProgressEntity.createDefault()
        val combinedUnlocked = current.getUnlockedLevelIds() + unlockedIds
        playerProgressDao.insertOrUpdateProgress(
            current.copy(
                unlockedLevelIdsJson = PlayerProgressEntity.serializeUnlockedIds(combinedUnlocked)
            )
        )
    }

    private suspend fun updatePlayerResumeAndStats(
        levelId: String,
        levelName: String,
        percentage: Int,
        highScore: Long,
        isPractice: Boolean
    ) {
        val current = playerProgressDao.getPlayerProgress() ?: PlayerProgressEntity.createDefault()
        val allMain = listOfNotNull(
            levelDao.getLevelById("main_stereo_madness"),
            levelDao.getLevelById("main_back_on_track"),
            levelDao.getLevelById("main_polargeist"),
            levelDao.getLevelById("main_dry_out"),
            levelDao.getLevelById("main_base_after_base"),
            levelDao.getLevelById("main_clubstep")
        )

        val totalStars = allMain.filter { it.completed }.sumOf { it.stars }
        val totalCoins = allMain.sumOf { it.coinsCollected }
        val demonsCompleted = allMain.count { it.completed && it.difficulty == Difficulty.DEMON.name }
        val totalAttempts = allMain.sumOf { it.attempts }
        val totalJumps = allMain.sumOf { it.jumps }

        // Find the furthest uncompleted campaign level to recommend or resume
        val nextCampaign = allMain.find { it.isUnlocked && !it.completed } ?: allMain.firstOrNull()

        playerProgressDao.insertOrUpdateProgress(
            current.copy(
                resumeLevelId = levelId,
                resumeLevelName = levelName,
                resumePercentage = percentage,
                resumeHighScore = maxOf(current.resumeHighScore, highScore),
                resumeIsPractice = isPractice,
                currentCampaignLevelId = nextCampaign?.id ?: levelId,
                totalStars = totalStars,
                totalCoins = totalCoins,
                demonsCompleted = demonsCompleted,
                totalAttempts = totalAttempts,
                totalJumps = totalJumps,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun rateLevel(
        id: String,
        score: Float, // 1 to 5
        difficultyVote: String,
        isLiked: Boolean
    ) {
        val current = levelDao.getLevelById(id) ?: return
        val currentCount = current.userRatingsCount
        val currentAvg = current.userRating
        val newCount = currentCount + 1
        val newAvg = ((currentAvg * currentCount) + score) / newCount
        val newLikes = if (isLiked) current.likesCount + 1 else current.likesCount
        levelDao.submitRating(
            id = id,
            newRating = newAvg,
            ratingsCount = newCount,
            likesCount = newLikes
        )
    }
}
