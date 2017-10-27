package org.jetbrains.kotlinconf

expect class Date() {
    fun getDate(): Int
    fun getMonth(): Int
    fun getFullYear(): Int
    fun getHours(): Int
    fun getMinutes(): Int
    fun getTime(): Number
}

expect operator fun Date.compareTo(otherDate: Date): Int

expect fun parseDate(dateString: String): Date
expect fun Date.toReadableDateString(): String
expect fun Date.toReadableTimeString(): String

fun Date.toReadableDateTimeString() = "${toReadableDateString()} ${toReadableTimeString()}"

