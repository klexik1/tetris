package com.khalchukov.tetris.ui.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khalchukov.tetris.data.db.GameResult
import com.khalchukov.tetris.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordsViewModel(private val container: AppContainer) : ViewModel() {
    val results: StateFlow<List<GameResult>> = container.results.top10
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clear() {
        viewModelScope.launch { container.results.clear() }
    }
}
