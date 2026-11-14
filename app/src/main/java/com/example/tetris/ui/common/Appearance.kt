package com.khalchukov.tetris.ui.common

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.khalchukov.tetris.data.prefs.AppLanguage
import com.khalchukov.tetris.data.prefs.AppSettings
import com.khalchukov.tetris.data.prefs.AppTheme

object Appearance {
    fun apply(settings: AppSettings) {
        applyTheme(settings.theme)
        applyLanguage(settings.language)
    }

    fun applyTheme(theme: AppTheme) {
        val mode = when (theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun applyLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.key))
    }
}
