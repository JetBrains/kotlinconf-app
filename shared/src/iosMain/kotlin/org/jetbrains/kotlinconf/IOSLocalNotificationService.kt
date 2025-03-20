package org.jetbrains.kotlinconf

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSTimeZone
import org.jetbrains.kotlinconf.utils.Logger
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

class IOSLocalNotificationService(
    private val timeProvider: TimeProvider,
    private val logger: Logger,
) : LocalNotificationService {
    companion object {
        const val LOCAL_NOTIFICATION_ID_KEY = "localNotificationId"
        private const val LOG_TAG = "IOSNotificationService"
    }

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine<Boolean> { continuation ->
            notificationCenter.requestAuthorizationWithOptions(UNAuthorizationOptionAlert) { granted, error ->
                continuation.resume(granted)
            }
        }
    }

    override fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime?
    ) {
        logger.log(LOG_TAG) { "Posting: $time, $localNotificationId, $title, $message" }

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setUserInfo(mapOf(LOCAL_NOTIFICATION_ID_KEY to localNotificationId.toString()))
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
        val request = UNNotificationRequest.requestWithIdentifier(localNotificationId.toString(), content, trigger)
        notificationCenter.addNotificationRequest(request) { error: NSError? ->
            logger.log(LOG_TAG) {
                if (error == null) {
                    "Notification request completed successfully"
                } else {
                    "Notification request failed with error: $error"
                }
            }
        }
    }

    override fun cancel(localNotificationId: LocalNotificationId) {
        logger.log(LOG_TAG) { "Cancelling: $localNotificationId" }
        val identifiers = listOf(localNotificationId.toString())
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
    }
}
