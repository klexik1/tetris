package com.khalchukov.tetris.ui.common

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val locale: Locale = Locale.forLanguageTag("ru-RU")

    fun number(value: Int): String = NumberFormat.getIntegerInstance(locale).format(value)

    fun duration(totalSec: Int): String = "%d:%02d".format(totalSec / 60, totalSec % 60)

    fun date(millis: Long): String =
        SimpleDateFormat("dd.MM.yyyy", locale).format(Date(millis))
}
