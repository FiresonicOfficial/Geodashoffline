package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.GameAudioEngine
import com.example.editor.LevelEditorState
import com.example.model.Level
import com.example.ui.CustomLevelsScreen
import com.example.ui.EditorScreen
import com.example.ui.GamePlayScreen
import com.example.ui.LevelSelectScreen
import com.example.ui.MainMenuScreen
import com.example.ui.StatsScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    MAIN_MENU,
    MAIN_LEVELS,
    CUSTOM_LEVELS,
    GAME_PLAY,
    EDITOR,
    STATS
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeoDashApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GameAudioEngine.start()
    }

    override fun onPause() {
        super.onPause()
        GameAudioEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        GameAudioEngine.stop()
    }
}

@Composable
fun GeoDashApp(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
    var activePlayLevel by remember { mutableStateOf<Level?>(null) }
    var activeIsPractice by remember { mutableStateOf(false) }
    var returnToEditorAfterPlay by remember { mutableStateOf(false) }
    var editorState by remember { mutableStateOf<LevelEditorState?>(null) }

    val mainLevels by viewModel.mainLevels.collectAsStateWithLifecycle()
    val customLevels by viewModel.customLevels.collectAsStateWithLifecycle()
    val allLevels by viewModel.allLevels.collectAsStateWithLifecycle()
    val playerProgress by viewModel.playerProgress.collectAsStateWithLifecycle()

    // Handle Android system back button
    BackHandler(enabled = currentScreen != Screen.MAIN_MENU) {
        when (currentScreen) {
            Screen.GAME_PLAY -> {
                if (returnToEditorAfterPlay) {
                    currentScreen = Screen.EDITOR
                    returnToEditorAfterPlay = false
                } else {
                    currentScreen = Screen.MAIN_MENU
                }
            }
            Screen.EDITOR -> currentScreen = Screen.CUSTOM_LEVELS
            Screen.MAIN_LEVELS, Screen.CUSTOM_LEVELS, Screen.STATS -> currentScreen = Screen.MAIN_MENU
            Screen.MAIN_MENU -> {}
        }
    }

    when (currentScreen) {
        Screen.MAIN_MENU -> {
            MainMenuScreen(
                playerProgress = playerProgress,
                onResumeCampaign = {
                    val targetId = playerProgress?.resumeLevelId ?: "main_stereo_madness"
                    val targetLevel = mainLevels.find { it.id == targetId } ?: mainLevels.firstOrNull()
                    if (targetLevel != null) {
                        activePlayLevel = targetLevel
                        activeIsPractice = playerProgress?.resumeIsPractice ?: false
                        returnToEditorAfterPlay = false
                        currentScreen = Screen.GAME_PLAY
                    }
                },
                onOpenMainLevels = { currentScreen = Screen.MAIN_LEVELS },
                onOpenEditor = {
                    editorState = LevelEditorState(null)
                    currentScreen = Screen.EDITOR
                },
                onOpenCustomLevels = { currentScreen = Screen.CUSTOM_LEVELS },
                onOpenStats = { currentScreen = Screen.STATS }
            )
        }

        Screen.MAIN_LEVELS -> {
            LevelSelectScreen(
                levels = mainLevels,
                onPlayLevel = { level, isPractice ->
                    activePlayLevel = level
                    activeIsPractice = isPractice
                    returnToEditorAfterPlay = false
                    currentScreen = Screen.GAME_PLAY
                },
                onBack = { currentScreen = Screen.MAIN_MENU }
            )
        }

        Screen.CUSTOM_LEVELS -> {
            CustomLevelsScreen(
                customLevels = customLevels,
                onPlayLevel = { level, isPractice ->
                    activePlayLevel = level
                    activeIsPractice = isPractice
                    returnToEditorAfterPlay = false
                    currentScreen = Screen.GAME_PLAY
                },
                onEditLevel = { level ->
                    editorState = LevelEditorState(level)
                    currentScreen = Screen.EDITOR
                },
                onCreateNewLevel = {
                    editorState = LevelEditorState(null)
                    currentScreen = Screen.EDITOR
                },
                onDeleteLevel = { id ->
                    viewModel.deleteLevel(id)
                },
                onRateLevel = { level, score, diff, liked ->
                    viewModel.rateLevel(level, score, diff, liked)
                },
                onImportLevel = { level ->
                    viewModel.saveCustomLevel(level)
                },
                onBack = { currentScreen = Screen.MAIN_MENU }
            )
        }

        Screen.GAME_PLAY -> {
            activePlayLevel?.let { level ->
                GamePlayScreen(
                    level = level,
                    isPracticeMode = activeIsPractice,
                    onFinish = { percentage, completed, attempts, jumps, coins ->
                        viewModel.recordAttempt(
                            id = level.id,
                            newPercentage = percentage,
                            completed = completed,
                            additionalAttempts = attempts,
                            additionalJumps = jumps,
                            newCoins = coins,
                            isPractice = activeIsPractice
                        )
                        if (returnToEditorAfterPlay) {
                            currentScreen = Screen.EDITOR
                            returnToEditorAfterPlay = false
                        } else {
                            currentScreen = Screen.MAIN_MENU
                        }
                    },
                    onRateLevel = { score, diff, liked ->
                        viewModel.rateLevel(level, score, diff, liked)
                    }
                )
            }
        }

        Screen.EDITOR -> {
            val state = editorState ?: remember { LevelEditorState(null) }.also { editorState = it }
            EditorScreen(
                editorState = state,
                onSaveLevel = { lvl ->
                    viewModel.saveCustomLevel(lvl)
                },
                onPlaytest = { lvl ->
                    activePlayLevel = lvl
                    activeIsPractice = true
                    returnToEditorAfterPlay = true
                    currentScreen = Screen.GAME_PLAY
                },
                onExit = { currentScreen = Screen.CUSTOM_LEVELS }
            )
        }

        Screen.STATS -> {
            StatsScreen(
                levels = allLevels,
                onBack = { currentScreen = Screen.MAIN_MENU }
            )
        }
    }
}

// Backward compatibility composable for tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
