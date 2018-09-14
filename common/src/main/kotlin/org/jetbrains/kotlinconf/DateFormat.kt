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

fun GMTDate.toReadableTimeString(): String = "${readableHours()}:${minutes.asMinutesString()} ${timeSuffix()}"
fun GMTDate.toReadableDateString(): String = "$year ${month.value} $dayOfMonth"
fun GMTDate.toReadableDateTimeString() = "${toReadableDateString()} ${toReadableTimeString()}"

/**
 * According to mask:
 */
fun String.parseDate(): GMTDate {
    val year = substring(0, 4).toInt()
    val month = substring(5, 7).toInt()
    val day = substring(8, 10).toInt()

    val hour = substring(11, 13).toInt()
    val minute = substring(14, 16).toInt()
    val second = substring(17, 19).toInt()

    return GMTDate(second, minute, hour, day, Month.from(month - 1), year)
}

fun GMTDate.parseToString(): String {
    val monthPart = "${month.ordinal + 1}".padStart(2, '0')
    val dayPart = "$dayOfMonth".padStart(2, '0')
    val hourPart = "$hours".padStart(2, '0')
    val minutePart = "$minutes".padStart(2, '0')
    val secondPart = "$seconds".padStart(2, '0')
    return "$year-$monthPart-$dayPart'T'$hourPart:$minutePart:$secondPart"
}
