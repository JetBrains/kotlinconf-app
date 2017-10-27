package org.jetbrains.kotlinconf

actual external class Date {
    actual constructor()
    constructor(value: Number)

    actual fun getDate(): Int
    actual fun getMonth(): Int
    actual fun getFullYear(): Int
    actual fun getHours(): Int
    actual fun getMinutes(): Int

    actual fun getTime(): Number

    companion object {
        fun parse(string: String): Number
    }
}

actual operator fun Date.compareTo(otherDate: Date) = getTime().toLong().compareTo(otherDate.getTime().toLong())

actual fun parseDate(dateString: String): Date = Date(Date.parse(dateString))

actual fun Date.toReadableDateString(): String {
    return "${monthAsString()} ${getDate()}, ${getFullYear()}"
}

actual fun Date.toReadableTimeString(): String = "${readableHours()}:${getMinutes().asMinutesString()} ${timeSuffix()}"

private fun Date.monthAsString(): String = months[getMonth()]

private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
