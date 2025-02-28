package org.jetbrains.kotlinconf

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume


class IOSNotificationService(
    private val timeProvider: TimeProvider,
) : NotificationService {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine<Boolean> { continuation ->
            notificationCenter.requestAuthorizationWithOptions(UNAuthorizationOptionAlert) { granted, error ->
                continuation.resume(granted)
            }
        }
    }

    override fun post(
        notificationId: String,
        title: String,
        message: String,
        time: LocalDateTime?
    ) {
        println("Posting: $time, $notificationId, $title, $message")

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
        }
        val trigger = if (time != null) {
            val adjustedTime = timeProvider.getNotificationTime(time)
            UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = NSDateComponents().apply {
                    year = adjustedTime.year.toLong()
                    month = adjustedTime.monthNumber.toLong()
                    day = adjustedTime.dayOfMonth.toLong()
                    hour = adjustedTime.hour.toLong()
                    minute = adjustedTime.minute.toLong()
                    second = adjustedTime.second.toLong()
                    val calendar = NSCalendar.currentCalendar
                    calendar.setTimeZone(EVENT_TIME_ZONE.toNSTimeZone())
                    this.calendar = calendar
                },
                repeats = false,
            )
        } else {
            null
        }
        val request = UNNotificationRequest.requestWithIdentifier(notificationId, content, trigger)
        notificationCenter.addNotificationRequest(request) { error: NSError? ->
            // TODO use logger
            println("Notification completed with: $error")
        }
    }

    override fun cancel(notificationId: String) {
        println("Cancelling: $notificationId")
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId))
    }
}
