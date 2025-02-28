package org.jetbrains.kotlinconf

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.utils.GRANTED_PERMISSION
import org.jetbrains.kotlinconf.utils.Notification
import kotlin.coroutines.resume

class ServiceWorkerNotificationService(
    private val timeProvider: TimeProvider,
) : NotificationService {
    override suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            Notification.requestPermission { result ->
                continuation.resume(result == GRANTED_PERMISSION)
            }
        }
    }

    override fun post(
        notificationId: String,
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
            notificationId = notificationId,
            title = title,
            message = message,
        )
    }

    override fun cancel(notificationId: String) {
        cancelNotificationByServiceWorker(notificationId)
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
