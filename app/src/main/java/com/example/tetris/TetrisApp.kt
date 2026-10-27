package com.khalchukov.tetris

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.khalchukov.tetris.di.AppContainer

class TetrisApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
