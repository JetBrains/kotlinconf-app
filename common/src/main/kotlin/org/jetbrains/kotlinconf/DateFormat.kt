package org.jetbrains.kotlinconf

import io.ktor.util.date.*

fun Pair<GMTDate, GMTDate>.toReadableString(): String = buildString {
    val (from, to) = this@toReadableString

    if (from.dayOfMonth != to.dayOfMonth || from.month != to.month || from.year != to.year) {
        append("${from.toReadableDateTimeString()}$emDash${to.toReadableDateTimeString()}")
        return@buildString
    }

    append(from.toReadableDateString() + ", ")

    val fromSuffix = from.timeSuffix()
    val toSuffix = to.timeSuffix()

    if (fromSuffix != toSuffix) {
        append("${from.toReadableTimeString()}$emDash{${to.toReadableTimeString()}}")
    } else {
        append("${from.readableHours()}:${from.minutes.asMinutesString()}")
        append(emDash)
        append("${to.readableHours()}:${to.minutes.asMinutesString()} $fromSuffix")
    }
}

fun GMTDate.timeSuffix() = if ((hours + 11) / 12 == 1) "a.m." else "p.m."
fun GMTDate.readableHours() = (hours + 11) % 12 + 1

fun Int.asMinutesString(): String = if (this < 10) "0$this" else toString()

private const val emDash = "\u2014"

//private val FULL_DATE_FORMATTER = createDateFormatter("EEEE, MMMM d h:mm a")
//private val ONLY_TIME_FORMATTER = createDateFormatter("h:mm a")
//private val WEEKDAY_TIME_FORMATTER = createDateFormatter("EEEE h:mm a")

fun GMTDate.toReadableTimeString(): String = TODO()
fun GMTDate.toReadableDateString(): String = TODO()
fun GMTDate.toReadableDateTimeString() = "${toReadableDateString()} ${toReadableTimeString()}"

fun parseDate(date: String): GMTDate = TODO()