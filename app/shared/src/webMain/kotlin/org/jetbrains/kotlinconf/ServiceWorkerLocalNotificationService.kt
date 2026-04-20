package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime
import org.koin.core.annotation.Singleton
import web.notifications.Notification
import web.notifications.NotificationPermission
import web.notifications.granted
import web.notifications.requestPermission
import kotlin.js.js

@Singleton
class ServiceWorkerLocalNotificationService(
    private val timeProvider: TimeProvider,
) : LocalNotificationService {
    override suspend fun requestPermission(): Boolean =
        Notification.requestPermission() == NotificationPermission.granted

    override fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime?,
    ) {
        val delay = if (time != null) {
            timeProvider.getNotificationDelay(time).inWholeMilliseconds
        } else {
            0
        }
        registerNotificationByServiceWorker(
            delay = delay,
            notificationId = localNotificationId.toString(),
            title = title,
            message = message,
        )
    }

    override fun cancel(localNotificationId: LocalNotificationId) {
        cancelNotificationByServiceWorker(localNotificationId.toString())
    }
}

private fun registerNotificationByServiceWorker(
    delay: Long,
    notificationId: String,
    title: String,
    message: String
): Unit =
    js(
        """{
        if (typeof navigator === "undefined" || navigator.serviceWorker == null || !navigator.serviceWorker.ready) return;
        navigator.serviceWorker.ready.then(function (registration) {
          registration.active.postMessage({
            command: 'register-notification',
            notificationId: notificationId, 
            title: title,
            body: message,
            delay: Number(delay),
          });
        });
        }"""
    )

private fun cancelNotificationByServiceWorker(notificationId: String): Unit =
    js(
        """{
        if (typeof navigator === "undefined" || navigator.serviceWorker == null || !navigator.serviceWorker.ready) return;
        navigator.serviceWorker.ready.then(function (registration) {
          registration.active.postMessage({
            command: 'cancel-notification',
            notificationId: notificationId,
          });
        });
        }"""
    )
