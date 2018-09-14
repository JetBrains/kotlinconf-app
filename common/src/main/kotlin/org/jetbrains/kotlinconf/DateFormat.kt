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
        append("${from.readableHours()}:${from.minutes.toString(2)}")
        append(emDash)
        append("${to.readableHours()}:${to.minutes.toString(2)} $fromSuffix")
    }
}

fun GMTDate.timeSuffix() = if ((hours + 11) / 12 == 1) "a.m." else "p.m."
fun GMTDate.readableHours() = (hours + 11) % 12 + 1

fun Int.toString(minSize: Int): String = "$this".padStart(minSize, '0')

private const val emDash = "\u2014"

fun GMTDate.toReadableTimeString(): String = "$hours:${minutes.toString(2)}"
fun GMTDate.toReadableDateString(): String = "$year ${month.value} $dayOfMonth"
fun GMTDate.toReadableDateTimeString() = "${toReadableDateString()} ${toReadableTimeString()}"

/**
 * According to mask: "yyyy-MM-dd'T'HH:mm:ss"
 */
fun String.parseDate(): GMTDate {
    fun formatError(): Nothing = throw Error("Format of $this is not correct")
    val year = substring(0, 4).toIntOrNull() ?: formatError()
    val month = substring(5, 7).toIntOrNull() ?: formatError()
    val day = substring(8, 10).toIntOrNull() ?: formatError()

    val hour = substring(11, 13).toIntOrNull() ?: formatError()
    val minute = substring(14, 16).toIntOrNull() ?: formatError()
    val second = substring(17, 19).toIntOrNull() ?: formatError()

    return GMTDate(second, minute, hour, day, Month.from(month - 1), year)
}

fun GMTDate.parseToString(): String {
    val monthPart = "${month.ordinal + 1}".padStart(2, '0')
    return "$year-$monthPart-${dayOfMonth.toString(2)}T${hours.toString(2)}:${minutes.toString(2)}:${seconds.toString(2)}"
}
