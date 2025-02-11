package org.jetbrains.kotlinconf

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDateComponents
import platform.Foundation.NSError

@OptIn(ExperimentalForeignApi::class)
internal fun LocalDateTime.toNSDateComponents(): NSDateComponents = NSDateComponents().apply {
    setYear(year.convert())
    setMonth(monthNumber.convert())
    setDay(dayOfMonth.convert())
    setHour(hour.convert())
    setMinute(minute.convert())
    setSecond(second.convert())
}

internal class NativeException(val error: NSError) : Exception()

internal fun NSError.asException(): NativeException = NativeException(this)
