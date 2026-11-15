package com.khalchukov.tetris.data.repository

import com.khalchukov.tetris.data.db.GameResult
import com.khalchukov.tetris.data.db.ResultDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultsRepository(private val dao: ResultDao) {
    val top10: Flow<List<GameResult>> = dao.top10()

    val bestScore: Flow<Int> = dao.bestScore().map { it ?: 0 }

    suspend fun saveResult(score: Int, lines: Int, level: Int, durationSec: Int): Boolean {
        val better = dao.countBetterThan(score)
        dao.insert(
            GameResult(
                score = score,
                lines = lines,
                level = level,
                durationSec = durationSec,
                playedAt = System.currentTimeMillis(),
            )
        )
        return score > 0 && better < 10
    }

    suspend fun clear() = dao.clear()
}
