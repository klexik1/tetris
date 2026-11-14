package com.khalchukov.tetris.game

import kotlin.math.max
import kotlin.math.pow

object Scoring {
    const val LINES_PER_LEVEL = 10
    const val SOFT_DROP_POINTS_PER_CELL = 1
    const val HARD_DROP_POINTS_PER_CELL = 2
    const val START_INTERVAL_MS = 1000.0
    const val MIN_INTERVAL_MS = 100.0
    private const val SPEEDUP = 0.9

    fun lineClearPoints(lines: Int, level: Int): Int = when (lines) {
        1 -> 100
        2 -> 300
        3 -> 500
        4 -> 800
        else -> 0
    } * level

    fun levelFor(totalLines: Int): Int = totalLines / LINES_PER_LEVEL + 1

    fun dropIntervalMs(level: Int): Long =
        max(MIN_INTERVAL_MS, START_INTERVAL_MS * SPEEDUP.pow((level - 1).toDouble())).toLong()
}
