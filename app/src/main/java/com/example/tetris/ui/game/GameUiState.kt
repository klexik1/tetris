package com.khalchukov.tetris.ui.game

import com.khalchukov.tetris.game.Cell
import com.khalchukov.tetris.game.PieceType

enum class Phase { COUNTDOWN, RUNNING, PAUSED, GAME_OVER }

data class GameUiState(
    val board: List<List<PieceType?>>,
    val current: List<Cell>,
    val currentType: PieceType?,
    val ghost: List<Cell>,
    val flashRows: List<Int>,
    val next: PieceType,
    val score: Int,
    val level: Int,
    val lines: Int,
    val phase: Phase,
    val countdown: Int,
    val elapsedSec: Int,
    val isNewRecord: Boolean,
)
