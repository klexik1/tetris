package com.khalchukov.tetris.game

data class PieceState(val type: Int, val rotation: Int, val x: Int, val y: Int)

data class GameSnapshot(
    val board: List<List<Int>>,
    val current: PieceState?,
    val next: Int,
    val score: Int,
    val level: Int,
    val lines: Int,
    val elapsedSec: Int,
)
