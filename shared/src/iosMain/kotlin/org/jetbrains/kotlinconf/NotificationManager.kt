package org.jetbrains.kotlinconf

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationManager actual constructor(context: ApplicationContext) {
    private val center: UNUserNotificationCenter =
        UNUserNotificationCenter.currentNotificationCenter()

    actual fun requestPermission() {
        val block = object : (Boolean, NSError?) -> Unit {
            override fun invoke(p1: Boolean, p2: NSError?) {}
        }

        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert, block)
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

        val request = UNNotificationRequest.requestWithIdentifier(title, content, trigger)
        val withCompletionHandler: (NSError?) -> Unit = { error: NSError? ->
            println("Notification completed with: $error")
        }

        center.addNotificationRequest(request, withCompletionHandler)
        return title
    }

    actual fun cancel(title: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(title))
    }
}