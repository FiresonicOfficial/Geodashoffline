package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LevelHighScoreEntity
import com.example.data.LevelRepository
import com.example.data.PlayerProgressEntity
import com.example.model.Difficulty
import com.example.model.Level
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LevelRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LevelRepository(db.levelDao(), db.playerProgressDao())
    }

    val mainLevels: StateFlow<List<Level>> = repository.mainLevels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customLevels: StateFlow<List<Level>> = repository.customLevels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLevels: StateFlow<List<Level>> = repository.allLevels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playerProgress: StateFlow<PlayerProgressEntity?> = repository.playerProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val highScores: StateFlow<List<LevelHighScoreEntity>> = repository.highScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordAttempt(
        id: String,
        newPercentage: Int,
        completed: Boolean,
        additionalAttempts: Int,
        additionalJumps: Int,
        newCoins: Int,
        isPractice: Boolean = false
    ) {
        viewModelScope.launch {
            repository.recordAttempt(
                id = id,
                newPercentage = newPercentage,
                completed = completed,
                additionalAttempts = additionalAttempts,
                additionalJumps = additionalJumps,
                newCoins = newCoins,
                isPractice = isPractice
            )
        }
    }

    fun saveCustomLevel(level: Level) {
        viewModelScope.launch {
            repository.saveCustomLevel(level)
        }
    }

    fun deleteLevel(id: String) {
        viewModelScope.launch {
            repository.deleteLevel(id)
        }
    }

    fun rateLevel(level: Level, score: Float, difficultyVote: Difficulty, isLiked: Boolean) {
        viewModelScope.launch {
            repository.rateLevel(
                id = level.id,
                score = score,
                difficultyVote = difficultyVote.name,
                isLiked = isLiked
            )
        }
    }
}
