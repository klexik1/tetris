package com.khalchukov.tetris.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    private val store = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = store.data.map { it.toSettings() }

    suspend fun setControls(value: ControlScheme) = store.edit { it[KEY_CONTROLS] = value.key }
    suspend fun setGhost(value: Boolean) = store.edit { it[KEY_GHOST] = value }
    suspend fun setGrid(value: Boolean) = store.edit { it[KEY_GRID] = value }

    private fun Preferences.toSettings() = AppSettings(
        controls = ControlScheme.fromKey(this[KEY_CONTROLS]),
        ghost = this[KEY_GHOST] ?: true,
        grid = this[KEY_GRID] ?: true,
    )

    private companion object {
        val KEY_CONTROLS = stringPreferencesKey("controls")
        val KEY_GHOST = booleanPreferencesKey("ghost")
        val KEY_GRID = booleanPreferencesKey("grid")
    }
}
