package com.khalchukov.tetris.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Insert
    suspend fun insert(result: GameResult): Long

    @Query("SELECT * FROM game_results ORDER BY score DESC, playedAt ASC LIMIT 10")
    fun top10(): Flow<List<GameResult>>

    @Query("SELECT MAX(score) FROM game_results")
    fun bestScore(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM game_results WHERE score > :score")
    suspend fun countBetterThan(score: Int): Int

    @Query("DELETE FROM game_results")
    suspend fun clear()
}
