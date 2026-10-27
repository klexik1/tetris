package com.khalchukov.tetris.di

import android.content.Context
import com.khalchukov.tetris.data.db.AppDatabase
import com.khalchukov.tetris.data.prefs.SettingsRepository
import com.khalchukov.tetris.data.repository.ResultsRepository
import com.khalchukov.tetris.data.save.SavedGameStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val database = AppDatabase.build(appContext)

    val results = ResultsRepository(database.resultDao())
    val settings = SettingsRepository(appContext)
    val savedGame = SavedGameStorage(appContext.filesDir)
}
