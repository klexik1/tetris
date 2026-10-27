package com.khalchukov.tetris.data.save

import com.khalchukov.tetris.game.GameSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SavedGameStorage(private val dir: File) {
    private val file: File get() = File(dir, FILE_NAME)

    suspend fun exists(): Boolean = withContext(Dispatchers.IO) { file.exists() }

    suspend fun save(snapshot: GameSnapshot) = withContext(Dispatchers.IO) {
        file.writeText(SnapshotJson.toJson(snapshot))
    }

    suspend fun load(): GameSnapshot? = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext null
        try {
            SnapshotJson.fromJson(file.readText())
        } catch (e: Exception) {
            file.delete()
            null
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        file.delete()
        Unit
    }

    private companion object {
        const val FILE_NAME = "saved_game.json"
    }
}
