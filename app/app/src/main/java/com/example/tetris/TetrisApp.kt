package com.khalchukov.tetris

import android.app.Application
import com.khalchukov.tetris.di.AppContainer
import com.khalchukov.tetris.ui.common.Appearance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TetrisApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        val settings = runBlocking { container.settings.settings.first() }
        Appearance.apply(settings)
    }
}
