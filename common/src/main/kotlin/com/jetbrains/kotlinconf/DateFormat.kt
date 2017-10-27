package org.jetbrains.kotlinconf

fun Pair<Date, Date>.toReadableString(): String {
    val (from, to) = this
    var result = ""

    if (from.getDate() != to.getDate() || from.getMonth() != to.getMonth() || from.getFullYear() != to.getFullYear()) {
        result += "${from.toReadableDateTimeString()}$emDash${to.toReadableDateTimeString()}"
    }
    else {
        result += from.toReadableDateString() + ", "
        val fromSuffix = from.timeSuffix()
        val toSuffix = to.timeSuffix()
        result += if (fromSuffix != toSuffix) {
            "${from.toReadableTimeString()}$emDash{${to.toReadableTimeString()}}"
        }
        else {
            "${from.readableHours()}:${from.getMinutes().asMinutesString()}" +
                    emDash +
                    "${to.readableHours()}:${to.getMinutes().asMinutesString()} $fromSuffix"
        }
    }

    return result
}

fun Date.timeSuffix() = if ((getHours() + 11) / 12 == 1) "a.m." else "p.m."

fun Date.readableHours() = (getHours() + 11) % 12 + 1

fun Int.asMinutesString(): String = if (this < 10) "0$this" else toString()

private const val emDash = "\u2014"
