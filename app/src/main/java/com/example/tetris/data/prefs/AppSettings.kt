package com.khalchukov.tetris.data.prefs

enum class ControlScheme(val key: String) {
    GESTURES("gestures"),
    BUTTONS("buttons");

    companion object {
        fun fromKey(key: String?): ControlScheme = values().firstOrNull { it.key == key } ?: BUTTONS
    }
}

data class AppSettings(
    val controls: ControlScheme = ControlScheme.BUTTONS,
    val ghost: Boolean = true,
    val grid: Boolean = true,
)
