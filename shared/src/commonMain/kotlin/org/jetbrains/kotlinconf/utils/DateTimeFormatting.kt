package org.jetbrains.kotlinconf.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

object DateTimeFormatting {
    private val timeFormat = LocalDateTime.Format {
        hour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
    }

    private val dateFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth()
    }

    private val dateWithYearFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth()
        chars(", ")
        year()
    }

    internal fun time(dateTime: LocalDateTime): String = dateTime.format(timeFormat)

    internal fun date(dateTime: LocalDateTime): String = dateTime.format(dateFormat)

    internal fun dateWithYear(dateTime: LocalDateTime): String = dateTime.format(dateWithYearFormat)

    internal fun timeToTime(start: LocalDateTime, end: LocalDateTime): String = "${time(start)}-${time(end)}"

    internal fun dateAndTime(dateTime: LocalDateTime): String = "${date(dateTime)}, ${time(dateTime)}"
}
