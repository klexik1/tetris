package com.khalchukov.tetris.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khalchukov.tetris.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val container: AppContainer) : ViewModel() {
    val bestScore: StateFlow<Int> = container.results.bestScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    suspend fun hasSavedGame(): Boolean = container.savedGame.exists()
}
