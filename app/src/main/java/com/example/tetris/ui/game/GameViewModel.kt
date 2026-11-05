package com.khalchukov.tetris.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khalchukov.tetris.di.AppContainer
import com.khalchukov.tetris.game.GameEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    private val container: AppContainer,
    private val resume: Boolean,
) : ViewModel() {
    private var engine = GameEngine()
    private var elapsedSec = 0
    private var phase = Phase.COUNTDOWN
    private var countdown = COUNTDOWN_FROM
    private var isNewRecord = false
    private var loopJob: Job? = null
    private var clearJob: Job? = null

    private val _state = MutableStateFlow(buildState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            var restored = false
            if (resume) {
                val saved = container.savedGame.load()
                if (saved != null) {
                    runCatching { GameEngine(restoreFrom = saved) }.onSuccess {
                        engine = it
                        elapsedSec = saved.elapsedSec
                        restored = true
                    }
                }
            }
            if (!restored) container.savedGame.clear()
            startLoop(withCountdown = true)
        }
    }

    fun moveLeft() = act { engine.moveLeft() }
    fun moveRight() = act { engine.moveRight() }
    fun rotate() = act { engine.rotate() }
    fun softDrop() = act { engine.softDrop() }
    fun hardDrop() = act { engine.hardDrop() }

    fun pause() {
        if (phase != Phase.RUNNING && phase != Phase.COUNTDOWN) return
        loopJob?.cancel()
        phase = Phase.PAUSED
        publish()
    }

    fun resumeGame() {
        if (phase != Phase.PAUSED) return
        startLoop(withCountdown = false)
    }

    fun restart() {
        loopJob?.cancel()
        clearJob?.cancel()
        engine = GameEngine()
        elapsedSec = 0
        isNewRecord = false
        viewModelScope.launch { container.savedGame.clear() }
        startLoop(withCountdown = true)
    }

    fun onBackgrounded() {
        pause()
        saveIfNeeded()
    }

    private inline fun act(block: () -> Unit) {
        if (phase != Phase.RUNNING) return
        block()
        onEngineChanged()
    }

    private fun startLoop(withCountdown: Boolean) {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            if (withCountdown) {
                phase = Phase.COUNTDOWN
                for (n in COUNTDOWN_FROM downTo 1) {
                    countdown = n
                    publish()
                    delay(1000)
                }
            }
            phase = Phase.RUNNING
            publish()
            val timer = launch {
                while (true) {
                    delay(1000)
                    elapsedSec++
                    publish()
                }
            }
            try {
                while (true) {
                    delay(engine.dropIntervalMs())
                    if (!engine.isClearing) {
                        engine.tick()
                        onEngineChanged()
                    }
                }
            } finally {
                timer.cancel()
            }
        }
    }

    private fun onEngineChanged() {
        when {
            engine.isGameOver -> finishGame()
            engine.isClearing -> {
                publish()
                if (clearJob?.isActive != true) {
                    clearJob = viewModelScope.launch {
                        delay(FLASH_MS)
                        engine.finishClear()
                        onEngineChanged()
                    }
                }
            }
            else -> publish()
        }
    }

    private fun finishGame() {
        if (phase == Phase.GAME_OVER) return
        loopJob?.cancel()
        phase = Phase.GAME_OVER
        publish()
        val score = engine.score
        val lines = engine.lines
        val level = engine.level
        val duration = elapsedSec
        container.appScope.launch {
            val isNew = container.results.saveResult(score, lines, level, duration)
            container.savedGame.clear()
            withContext(Dispatchers.Main) {
                isNewRecord = isNew
                publish()
            }
        }
    }

    private fun saveIfNeeded() {
        if (phase == Phase.GAME_OVER || engine.isGameOver) return
        val snapshot = engine.snapshot(elapsedSec)
        container.appScope.launch { container.savedGame.save(snapshot) }
    }

    private fun publish() {
        _state.value = buildState()
    }

    private fun buildState(): GameUiState {
        val piece = engine.current
        return GameUiState(
            board = engine.boardSnapshot(),
            current = piece?.cells() ?: emptyList(),
            currentType = piece?.type,
            ghost = engine.ghostPiece()?.cells() ?: emptyList(),
            flashRows = engine.pendingClearRows,
            next = engine.next,
            score = engine.score,
            level = engine.level,
            lines = engine.lines,
            phase = phase,
            countdown = countdown,
            elapsedSec = elapsedSec,
            isNewRecord = isNewRecord,
        )
    }

    private companion object {
        const val COUNTDOWN_FROM = 3
        const val FLASH_MS = 180L
    }
}
