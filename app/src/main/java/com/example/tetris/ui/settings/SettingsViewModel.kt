package com.khalchukov.tetris.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khalchukov.tetris.data.prefs.AppSettings
import com.khalchukov.tetris.data.prefs.ControlScheme
import com.khalchukov.tetris.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    private val repo = container.settings

    val settings: StateFlow<AppSettings?> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setControls(value: ControlScheme) = viewModelScope.launch { repo.setControls(value) }
    fun setGhost(value: Boolean) = viewModelScope.launch { repo.setGhost(value) }
    fun setGrid(value: Boolean) = viewModelScope.launch { repo.setGrid(value) }
}
