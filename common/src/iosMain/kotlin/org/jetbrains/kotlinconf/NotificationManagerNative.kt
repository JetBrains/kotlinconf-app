package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.storage.*
import platform.Foundation.*
import platform.UserNotifications.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*

actual class NotificationManager actual constructor(context: ApplicationContext) {
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    actual fun requestPermission() {
        val block = object : (Boolean, NSError?) -> Unit {
            override fun invoke(p1: Boolean, p2: NSError?) {}
        }

        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert, block.freeze())
    }

    actual fun schedule(delay: Long, title: String, message: String): String? {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
        }

        val date = NSDate.dateWithTimeIntervalSinceNow(delay / 1000.0)

        val componentsSet =
            (NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond)

        val triggerDate = NSCalendar.currentCalendar.components(
            componentsSet, date
        )

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            triggerDate, repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(title, content, trigger).freeze()
        val withCompletionHandler: (NSError?) -> Unit = { error: NSError? -> }.freeze()

        center.addNotificationRequest(request, withCompletionHandler)
        return title
    }

    actual fun cancel(title: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(title))
    }
}

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
