package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.cinterop.*
import org.jetbrains.kotlinconf.storage.*
import platform.Foundation.*
import platform.UserNotifications.*

@OptIn(ExperimentalForeignApi::class)
internal fun GMTDate.toNSDateComponents(): NSDateComponents = NSDateComponents().apply {
    setYear(this@toNSDateComponents.year.convert())
    setMonth(this@toNSDateComponents.month.ordinal.convert())
    setDay(this@toNSDateComponents.dayOfMonth.convert())
    setHour(this@toNSDateComponents.hours.convert())
    setMinute(this@toNSDateComponents.minutes.convert())
    setSecond(this@toNSDateComponents.seconds.convert())
}

internal class NativeException(val error: NSError) : Exception()

internal fun NSError.asException(): NativeException = NativeException(this)
