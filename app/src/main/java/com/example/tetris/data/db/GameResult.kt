package com.khalchukov.tetris.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val lines: Int,
    val level: Int,
    val durationSec: Int,
    val playedAt: Long,
)
