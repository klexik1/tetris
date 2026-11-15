package com.khalchukov.tetris.data.prefs

enum class ControlScheme(val key: String) {
    GESTURES("gestures"),
    BUTTONS("buttons");

    companion object {
        fun fromKey(key: String?): ControlScheme = values().firstOrNull { it.key == key } ?: BUTTONS
    }
}

enum class AppTheme(val key: String) {
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(key: String?): AppTheme = values().firstOrNull { it.key == key } ?: LIGHT
    }
}

enum class AppLanguage(val key: String) {
    RU("ru"),
    EN("en");

    companion object {
        fun fromKey(key: String?): AppLanguage = values().firstOrNull { it.key == key } ?: RU
    }
}

data class AppSettings(
    val controls: ControlScheme = ControlScheme.BUTTONS,
    val ghost: Boolean = true,
    val grid: Boolean = true,
    val theme: AppTheme = AppTheme.LIGHT,
    val language: AppLanguage = AppLanguage.RU,
)
